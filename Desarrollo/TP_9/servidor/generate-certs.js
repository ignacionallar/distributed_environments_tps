const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const certsDir = path.join(__dirname, 'certs');
const keyPath = path.join(certsDir, 'server.key');
const certPath = path.join(certsDir, 'server.crt');

function generarCertificados() {
    if (!fs.existsSync(certsDir)) {
        fs.mkdirSync(certsDir, { recursive: true });
    }

    if (fs.existsSync(keyPath) && fs.existsSync(certPath)) {
        console.log('[TLS/SSL] Certificados SSL/TLS ya existentes en /certs.');
        return;
    }

    console.log('[TLS/SSL] Generando certificados autofirmados SSL/TLS con OpenSSL...');
    try {
        const cmd = `openssl req -x509 -newkey rsa:2048 -nodes -keyout "${keyPath}" -out "${certPath}" -days 365 -subj "/C=AR/ST=Salta/L=Salta/O=UNSa/OU=SistemasDistribuidas/CN=localhost"`;
        execSync(cmd, { stdio: 'inherit' });
        console.log('[TLS/SSL] Certificados TLS/SSL generados exitosamente.');
    } catch (err) {
        console.error('[TLS/SSL] Error al ejecutar OpenSSL. Asegúrese de tener OpenSSL en el PATH del sistema.');
        process.exit(1);
    }
}

if (require.main === module) {
    generarCertificados();
}

module.exports = { generarCertificados, keyPath, certPath };