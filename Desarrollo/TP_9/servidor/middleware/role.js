/**
 * Middleware para Control de Acceso Basado en Roles (RBAC)
 */
function verificarRol(...rolesPermitidos) {
    return (req, res, next) => {
        if (!req.user || !req.user.rol) {
            return res.status(403).json({
                error: 'Acceso prohibido: La identidad del usuario no posee un rol asignado',
                codigo: 'ROLE_UNDEFINED'
            });
        }

        if (!rolesPermitidos.includes(req.user.rol)) {
            return res.status(403).json({
                error: `Acceso prohibido: El rol '${req.user.rol}' no posee permisos para acceder a este recurso`,
                codigo: 'INSUFFICIENT_PERMISSIONS',
                rolRequerido: rolesPermitidos
            });
        }

        next();
    };
}

module.exports = {
    verificarRol
};