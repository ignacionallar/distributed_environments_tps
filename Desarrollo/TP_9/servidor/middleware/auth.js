const jwt = require('jsonwebtoken');

const SECRET_KEY = process.env.JWT_SECRET || 'secreto_super_seguro_distribuidas_tp9';

/**
 * Middleware para validar la firma y vigencia del JWT sobre HTTPS
 */
function verificarToken(req, res, next) {
    const authHeader = req.headers['authorization'];

    if (!authHeader) {
        return res.status(401).json({
            error: 'Acceso denegado: Encabezado Authorization no proporcionado',
            codigo: 'TOKEN_MISSING'
        });
    }

    if (!authHeader.startsWith('Bearer ')) {
        return res.status(401).json({
            error: 'Formato de autenticación inválido. Debe utilizar el esquema Bearer',
            codigo: 'TOKEN_MALFORMED'
        });
    }

    const token = authHeader.split(' ')[1];

    try {
        const decoded = jwt.verify(token, SECRET_KEY);
        req.user = decoded;
        next();
    } catch (err) {
        if (err.name === 'TokenExpiredError') {
            return res.status(401).json({
                error: 'Token expirado. Por favor, vuelva a autenticarse',
                codigo: 'TOKEN_EXPIRED',
                expiradoEn: err.expiredAt
            });
        }

        // Detecta alteración deliberada de payload (Tampering) o firma corrupta
        return res.status(401).json({
            error: 'Token inválido o alterado intencionadamente (Tampering detectado)',
            codigo: 'TOKEN_INVALID_OR_TAMPERED',
            detalle: err.message
        });
    }
}

module.exports = {
    verificarToken,
    SECRET_KEY
};