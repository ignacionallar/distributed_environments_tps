const express = require('express');
const app = express();

app.use(express.json());

const PORT = process.env.PORT || 3000;

// Base de datos simulada en memoria
const usuarios = Array.from({ length: 100 }, (_, i) => ({
    id: i + 1,
    nombre: `Usuario_${i + 1}`,
    email: `usuario${i + 1}@distribuidas.com`,
    creado_en: new Date().toISOString()
}));

// Simulador de demora I/O (Base de Datos)
const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

// --- ENDPOINTS PARA EVALUACIÓN DE DESEMPEÑO ---

// Healthcheck
app.get('/api/health', (req, res) => {
    res.status(200).json({ estado: 'OK', uptime: process.uptime() });
});

// Endpoint 1: Consulta Ligera de Lectura (Simula consulta optimizada)
app.get('/api/usuarios', async (req, res) => {
    // Simula una latencia de lectura de BD de 15ms
    await delay(15);
    res.status(200).json({
        total: usuarios.length,
        data: usuarios.slice(0, 20)
    });
});

// Endpoint 2: Escritura de Datos (Simula inserción en BD)
app.post('/api/usuarios', async (req, res) => {
    const { nombre, email } = req.body;

    if (!nombre || !email) {
        return res.status(400).json({ error: 'Nombre y email son requeridos' });
    }

    // Simula latencia de escritura e indexación en BD (45ms)
    await delay(45);

    const nuevoUsuario = {
        id: usuarios.length + 1,
        nombre,
        email,
        creado_en: new Date().toISOString()
    };

    usuarios.push(nuevoUsuario);
    res.status(201).json(nuevoUsuario);
});

// Endpoint 3: Operación Cómputo-Intensiva (Punto de saturación de CPU / Event Loop)
app.get('/api/procesar-reporte', (req, res) => {
    const inicio = Date.now();
    
    // Algoritmo intencionalmente pesado para forzar uso de CPU
    let hashSimulado = 0;
    for (let i = 0; i < 5_000_000; i++) {
        hashSimulado = (hashSimulado + Math.sqrt(i) * Math.sin(i)) % 1000000;
    }

    const duracion = Date.now() - inicio;

    res.status(200).json({
        mensaje: 'Reporte procesado exitosamente',
        tiempoProcesamientoMs: duracion,
        resultadoCalculado: Math.round(hashSimulado)
    });
});

app.listen(PORT, () => {
    console.log(`===================================================`);
    console.log(` Servidor de Pruebas de Carga Activo en Puerto ${PORT}`);
    console.log(` Endpoints expuestos:`);
    console.log(`   - GET  /api/usuarios (Lectura liviana)`);
    console.log(`   - POST /api/usuarios (Escritura)`);
    console.log(`   - GET  /api/procesar-reporte (Cómputo pesado CPU)`);
    console.log(`===================================================`);
});