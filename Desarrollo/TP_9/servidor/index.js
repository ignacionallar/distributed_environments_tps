const express = require('express');
const https = require('https');
const fs = require('fs');
const jwt = require('jsonwebtoken');

const { verificarToken, SECRET_KEY } = require('./middleware/auth');
const { verificarRol } = require('./middleware/role');
const { generarCertificados, keyPath, certPath } = require('./generate-certs');

// Garantizar disponibilidad de certificados SSL/TLS
generarCertificados();

const app = express();
app.use(express.json());

const PORT = process.env.PORT || 3443;

// Base de datos simulada
const usuariosDB = [
    {
        id: "1",
        usuario: "ignacio",
        password: "password123",
        nombre: "Ignacio Nallar",
        email: "ignacio@distribuidas.com",
        rol: "admin"
    },
    {
        id: "2",
        usuario: "invitado",
        password: "password123",
        nombre: "Usuario Invitado",
        email: "invitado@distribuidas.com",
        rol: "user"
    }
];

// --- ENDPOINTS PÚBLICOS ---

app.get('/api/publico', (req, res) => {
    res.status(200).json({
        estado: "OK",
        mensaje: "Canal seguro HTTPS activo sobre TLS/SSL",
        protocolo: req.protocol.toUpperCase(),
        timestamp: new Date().toISOString()
    });
});

app.post('/api/login', (req, res) => {
    const { usuario, password, duracion } = req.body;

    if (!usuario || !password) {
        return res.status(400).json({
            error: "Debe proporcionar usuario y contraseña"
        });
    }

    const user = usuariosDB.find(u => u.usuario === usuario && u.password === password);

    if (!user) {
        return res.status(401).json({
            error: "Credenciales inválidas"
        });
    }

    const payload = {
        sub: user.id,
        nombre: user.nombre,
        email: user.email,
        rol: user.rol
    };

    const expiresIn = duracion || '1h';
    const token = jwt.sign(payload, SECRET_KEY, { expiresIn });

    res.status(200).json({
        mensaje: "Autenticación exitosa",
        tipo: "Bearer",
        token: token,
        expiracion: expiresIn,
        usuario: {
            id: user.id,
            nombre: user.nombre,
            rol: user.rol
        }
    });
});

// --- ENDPOINTS PROTEGIDOS POR JWT Y RBAC ---

app.get('/api/perfil', verificarToken, (req, res) => {
    res.status(200).json({
        mensaje: "Acceso autorizado a datos del perfil por canal seguro HTTPS",
        usuarioAutenticado: req.user,
        seguridad: "TLS 1.3 / JWT Validado"
    });
});

app.get('/api/admin', verificarToken, verificarRol('admin'), (req, res) => {
    res.status(200).json({
        mensaje: "Bienvenido al Panel de Administración Distribuida",
        metricasSistema: {
            nodosActivos: 3,
            estadoSeguridad: "TLS/SSL Enforced",
            usuarioAdmin: req.user.nombre
        }
    });
});

// Cargar Certificados HTTPS
const sslOptions = {
    key: fs.readFileSync(keyPath),
    cert: fs.readFileSync(certPath)
};

// Crear e Iniciar Servidor HTTPS
const server = https.createServer(sslOptions, app);

server.listen(PORT, () => {
    console.log(`===================================================`);
    console.log(` Servidor HTTPS Seguro Activo en Puerto ${PORT}`);
    console.log(` Protocolo: HTTPS (TLS/SSL Cifrado en Tránsito)`);
    console.log(` URL Base:  https://localhost:${PORT}`);
    console.log(`===================================================`);
});