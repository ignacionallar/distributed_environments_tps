const express = require('express');
const { Pool } = require('pg');

const app = express();
app.use(express.json());

// Pool para el NODO PRIMARIO (Puerto 5432 - Lecturas / Escrituras)
const poolPrimary = new Pool({
    host: 'localhost',
    port: 5432,
    user: 'admin',
    password: 'adminpassword',
    database: 'distribuida_db',
    connectionTimeoutMillis: 3000
});

poolPrimary.on('error', (err) => {
    console.error('[Pool Primario] Evento de error:', err.message);
});

// Pool para el NODO SECUNDARIO (Puerto 5433 - Réplica Solo Lectura)
const poolSecondary = new Pool({
    host: 'localhost',
    port: 5433,
    user: 'admin',
    password: 'adminpassword',
    database: 'distribuida_db',
    connectionTimeoutMillis: 3000
});

poolSecondary.on('error', (err) => {
    console.error('[Pool Secundario] Evento de error:', err.message);
});

// Ruta de ESCRITURA (Canalizada al Nodo Primario)
app.post('/api/usuarios', async (req, res) => {
    const { nombre, email } = req.body;
    if (!nombre || !email) {
        return res.status(400).json({ error: "Faltan campos obligatorios: nombre, email." });
    }

    console.log("[Backend] Solicitud de ESCRITURA enviada a db-primary (Puerto 5432)...");
    try {
        const query = 'INSERT INTO usuarios (nombre, email) VALUES ($1, $2) RETURNING *';
        const result = await poolPrimary.query(query, [nombre, email]);
        
        console.log(`[Backend] ESCRITURA exitosa en Primario. Registro ID: ${result.rows[0].id}`);
        res.status(201).json({
            mensaje: "Registro insertado exitosamente en Nodo Primario",
            nodo: "db-primary (Puerto 5432)",
            data: result.rows[0]
        });
    } catch (error) {
        console.error("[Backend] ERROR EN ESCRITURA (Primario):", error.message);
        res.status(500).json({
            error: "Fallo de comunicación con el Nodo Primario",
            detalle: error.message
        });
    }
});

// Ruta de LECTURA (Canalizada al Nodo Secundario / Réplica)
app.get('/api/usuarios', async (req, res) => {
    console.log("[Backend] Solicitud de LECTURA enviada a db-secondary (Puerto 5433)...");
    try {
        const query = 'SELECT * FROM usuarios ORDER BY id DESC';
        const result = await poolSecondary.query(query);
        
        console.log(`[Backend] LECTURA exitosa en Secundario: ${result.rows.length} registros devueltos.`);
        res.status(200).json({
            origen: "db-secondary (Puerto 5433 - Réplica Hot Standby)",
            totalRegistros: result.rows.length,
            data: result.rows
        });
    } catch (error) {
        console.error("[Backend] ERROR EN LECTURA (Secundario):", error.message);
        res.status(500).json({
            error: "Fallo de comunicación con la Réplica Secundaria",
            detalle: error.message
        });
    }
});

// Prueba de Intento de Escritura directa en la Réplica (Validación Read-Only)
app.post('/api/usuarios/test-replica-write', async (req, res) => {
    const { nombre, email } = req.body;
    console.log("[Backend] Probando ESCRITURA directa en db-secondary (Puerto 5433)...");
    try {
        const query = 'INSERT INTO usuarios (nombre, email) VALUES ($1, $2) RETURNING *';
        await poolSecondary.query(query, [nombre || 'Test', email || 'test@test.com']);
        res.status(200).json({ mensaje: "Inesperado: Se permitió escribir en la réplica." });
    } catch (error) {
        console.log("[Backend] Réplica rechazó la escritura correctamente:", error.message);
        res.status(403).json({
            mensaje: "Operación rechazada correctamente por la réplica de solo lectura",
            errorPostgres: error.message
        });
    }
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Servidor Backend corriendo en http://localhost:${PORT}`);
    console.log("Conexiones configuradas:");
    console.log(" - Primario   : localhost:5432");
    console.log(" - Secundario : localhost:5433");
});