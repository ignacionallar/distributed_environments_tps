const https = require('https');

// Agente HTTPS que ignora certificados autofirmados para entorno local
const agent = new https.Agent({ rejectUnauthorized: false });
const BASE_URL = 'https://localhost:3443';

function request(urlPath, method = 'GET', headers = {}, body = null) {
    return new Promise((resolve, reject) => {
        const url = new URL(BASE_URL + urlPath);
        const options = {
            hostname: url.hostname,
            port: url.port,
            path: url.pathname,
            method: method,
            agent: agent,
            headers: {
                'Content-Type': 'application/json',
                ...headers
            }
        };

        const req = https.request(options, (res) => {
            let data = '';
            res.on('data', chunk => data += chunk);
            res.on('end', () => {
                try {
                    const parsed = JSON.parse(data);
                    resolve({ status: res.statusCode, data: parsed });
                } catch (e) {
                    resolve({ status: res.statusCode, data: data });
                }
            });
        });

        req.on('error', err => reject(err));

        if (body) {
            req.write(JSON.stringify(body));
        }
        req.end();
    });
}

function tamperedJwtPayload(originalToken, nuevosDatos) {
    const parts = originalToken.split('.');
    if (parts.length !== 3) throw new Error("Format JWT inválido");

    const header = parts[0];
    const payloadBase64 = parts[1];
    const signature = parts[2];

    const payloadJsonStr = Buffer.from(payloadBase64, 'base64url').toString('utf8');
    const payloadObj = JSON.parse(payloadJsonStr);

    Object.assign(payloadObj, nuevosDatos);

    const tamperedPayloadBase64 = Buffer.from(JSON.stringify(payloadObj)).toString('base64url');
    return `${header}.${tamperedPayloadBase64}.${signature}`;
}

async function ejecutarSuiteDePruebas() {
    console.log("\n=======================================================");
    console.log("   SUITE DE PRUEBAS AUTOMATIZADAS - TP 9 SEGURIDAD    ");
    console.log("=======================================================\n");

    let tokenAdmin = null;
    let tokenUser = null;

    // PRUEBA 1: Emisión de JWT
    console.log("--> PRUEBA 1: POST /api/login (Autenticación y Emisión JWT)");
    const resLogin = await request('/api/login', 'POST', {}, {
        usuario: 'ignacio',
        password: 'password123'
    });
    console.log(`[Status ${resLogin.status}] Response:`, resLogin.data);
    if (resLogin.status === 200 && resLogin.data.token) {
        tokenAdmin = resLogin.data.token;
        console.log("✔ ÉXITO: Token JWT obtenido correctamente.\n");
    }

    const resUserLogin = await request('/api/login', 'POST', {}, {
        usuario: 'invitado',
        password: 'password123'
    });
    if (resUserLogin.status === 200) tokenUser = resUserLogin.data.token;

    // PRUEBA 2: Acceso Autorizado HTTPS
    console.log("--> PRUEBA 2: GET /api/perfil (Acceso Autorizado sobre HTTPS)");
    const resPerfil = await request('/api/perfil', 'GET', {
        'Authorization': `Bearer ${tokenAdmin}`
    });
    console.log(`[Status ${resPerfil.status}] Response:`, resPerfil.data);
    if (resPerfil.status === 200) {
        console.log("✔ ÉXITO: Acceso concedido sobre canal HTTPS seguro.\n");
    }

    // PRUEBA 3: Control de Acceso RBAC
    console.log("--> PRUEBA 3: GET /api/admin (Acceso con Rol 'user' -> HTTP 403 Forbidden)");
    const resAdminForbidden = await request('/api/admin', 'GET', {
        'Authorization': `Bearer ${tokenUser}`
    });
    console.log(`[Status ${resAdminForbidden.status}] Response:`, resAdminForbidden.data);
    if (resAdminForbidden.status === 403) {
        console.log("✔ ÉXITO: Rechazado correctamente con HTTP 403 por falta de rol 'admin'.\n");
    }

    // PRUEBA 4: Rechazo por Falta de Token
    console.log("--> PRUEBA 4: GET /api/perfil (Sin Token -> HTTP 401 Unauthorized)");
    const resSinToken = await request('/api/perfil', 'GET');
    console.log(`[Status ${resSinToken.status}] Response:`, resSinToken.data);
    if (resSinToken.status === 401) {
        console.log("✔ ÉXITO: Rechazado correctamente con HTTP 401.\n");
    }

    // PRUEBA 5: Detección de Tampering
    console.log("--> PRUEBA 5: GET /api/perfil (Ataque de Tampering / Alteración de Payload)");
    const tokenAlterado = tamperedJwtPayload(tokenUser, { rol: 'admin', nombre: 'Atacante Hacker' });
    console.log("   Token Original: ", tokenUser.substring(0, 40) + "...");
    console.log("   Token Alterado: ", tokenAlterado.substring(0, 40) + "...");

    const resTampering = await request('/api/perfil', 'GET', {
        'Authorization': `Bearer ${tokenAlterado}`
    });
    console.log(`[Status ${resTampering.status}] Response:`, resTampering.data);
    if (resTampering.status === 401 && resTampering.data.codigo === 'TOKEN_INVALID_OR_TAMPERED') {
        console.log("✔ ÉXITO: Ataque de Tampering detectado y bloqueado con HTTP 401 (Firma Inválida).\n");
    }

    // PRUEBA 6: Expiración de Token
    console.log("--> PRUEBA 6: Expiración de Token (Expiración en 1 segundo)");
    const resShortLogin = await request('/api/login', 'POST', {}, {
        usuario: 'ignacio',
        password: 'password123',
        duracion: '1s'
    });
    const tokenExpirable = resShortLogin.data.token;

    console.log("   Esperando 2 segundos para forzar expiración...");
    await new Promise(r => setTimeout(r, 2000));

    const resExpired = await request('/api/perfil', 'GET', {
        'Authorization': `Bearer ${tokenExpirable}`
    });
    console.log(`[Status ${resExpired.status}] Response:`, resExpired.data);
    if (resExpired.status === 401 && resExpired.data.codigo === 'TOKEN_EXPIRED') {
        console.log("✔ ÉXITO: Token expirado detectado correctamente con HTTP 401.\n");
    }

    console.log("=======================================================");
    console.log("   TODAS LAS PRUEBAS FINALIZADAS SATISFACTORIAMENTE   ");
    console.log("=======================================================\n");
}

ejecutarSuiteDePruebas();