<details>
<summary>TP 8 - Bases de Datos Distribuidas y Conexión entre Capas</summary>

## 1. Esquema de Arquitectura (Replicación Primario / Respaldo-Primario)

```mermaid
sequenceDiagram
    participant Cliente as App Cliente / Postman
    participant Backend as Backend Node.js
    participant Primario as DB Nodo Primario (Port 5432)
    participant Secundario as DB Nodo Secundario (Port 5433)

    rect rgb(230, 245, 230)
    Note over Cliente, Secundario: Replicación Streaming en Tiempo Real (WAL)
    Primario->>Secundario: Transmisión continua de registros WAL
    end

    rect rgb(230, 245, 230)
    Note over Cliente, Primario: Flujo de Escritura
    Cliente->>Backend: POST /api/usuarios
    Backend->>Primario: INSERT INTO usuarios ...
    Primario-->>Backend: OK + Registro ID
    Backend-->>Cliente: Status 201 Created
    end

    rect rgb(230, 230, 245)
    Note over Cliente, Secundario: Flujo de Lectura
    Cliente->>Backend: GET /api/usuarios
    Backend->>Secundario: SELECT * FROM usuarios ...
    Secundario-->>Backend: OK + Datos Replicados
    Backend-->>Cliente: Status 200 OK + Payload
    end

    rect rgb(255, 230, 230)
    Note over Backend, Primario: Simulación de Caída de Nodo (Failover Test)
    Backend->>Primario: INSERT (Nodo detenido con docker stop)
    Primario--xBackend: Connection Refused / Timeout
    Backend-->>Cliente: Status 500 Error Manejado
    Cliente->>Backend: GET /api/usuarios
    Backend->>Secundario: SELECT (Nodo Secundario sigue activo)
    Secundario-->>Backend: OK + Registros
    Backend-->>Cliente: Status 200 OK (Lecturas Operativas)
    end
    ```