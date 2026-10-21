# Trabajo Práctico N° 7 — Integración entre Capas y Consumo de APIs / Servicios Distribuidos

## Instrucciones de Ejecución

1. **Abrir terminal (Ctrl + Alt + Ñ)** y posicionarse en la carpeta `Desarrollo/TP_7`.
2. java servidor/ServidorAPI.java
3. **Abrir terminal (Ctrl + Alt + Ñ)**
4. java cliente/ClienteAPI.java
5. El cliente muestra un menú para listar, obtener y crear productos.

## Diagrama de Arquitectura

```mermaid
sequenceDiagram
    autonumber
    participant Cli as "ClienteAPI (Consola)"
    participant HTTP as "Canal HTTP/1.1 (TCP :8080)"
    participant Srv as "ServidorAPI (HttpServer)"

    Note over Cli: Proceso independiente<br/>java cliente/ClienteAPI.java
    Note over Srv: Proceso independiente<br/>java servidor/ServidorAPI.java

    rect rgb(230, 240, 255)
    Note over Cli,Srv: Captura 1 — GET /productos (200 OK)
    Cli->>HTTP: GET /productos (HTTP/1.1)
    HTTP->>Srv: GET /productos
    Srv->>Srv: manejarProductos()<br/>print("GET /productos desde ...")
    Srv->>Srv: marshalling JSON (listarProductosJson)
    Srv-->>HTTP: 200 OK + JSON
    HTTP-->>Cli: 200 OK + JSON
    Cli->>Cli: unmarshalling (contarOcurrencias)<br/>print("Productos deserializados: N")
    end

    rect rgb(230, 255, 230)
    Note over Cli,Srv: Captura 1 — POST /productos (201 Created)
    Cli->>Cli: marshalling payload JSON
    Cli->>HTTP: POST /productos (payload JSON)
    HTTP->>Srv: POST /productos
    Srv->>Srv: print("Payload recibido -> ...")<br/>unmarshalling (parsearProducto)
    Srv->>Srv: marshalling respuesta (creado.toJson)
    Srv-->>HTTP: 201 Created + JSON
    HTTP-->>Cli: 201 Created + JSON
    Cli->>Cli: print estado + JSON
    end

    rect rgb(255, 240, 230)
    Note over Cli,Srv: Captura 3 — Manejo de errores
    Cli->>HTTP: GET /productos/999
    HTTP->>Srv: GET /productos/999
    Srv->>Srv: buscarProducto(999) -> null
    Srv-->>HTTP: 404 Not Found + JSON
    HTTP-->>Cli: 404 Not Found + JSON
    Cli->>Cli: print estado 404

    Cli->>HTTP: POST /productos (payload inválido)
    HTTP->>Srv: POST /productos
    Srv->>Srv: parsearProducto() -> null
    Srv-->>HTTP: 400 Bad Request + JSON
    HTTP-->>Cli: 400 Bad Request + JSON
    Cli->>Cli: print estado 400
    end

    Note over Cli: try/catch en enviarGet() y enviarPost()<br/>captura timeout/conexión (retorna null)
    Note over Srv: 405 Method Not Allowed y 500 Internal Server Error<br/>también manejados
    Note over Cli,Srv: Sin frameworks externos: JDK HttpServer + HttpClient
```

## Descripción del Servicio

API REST de **Catálogo de Productos** con los siguientes endpoints:

| Método | Ruta | Descripción | Códigos de estado |

GET -> `/productos` -> Lista todos los productos -> 200 
GET -> `/productos/{id}` -> Obtiene un producto por id -> 200, 404 
POST -> `/productos` -> Crea un producto (JSON) -> 201, 400 

El servidor registra en consola cada petición (método, ruta, host del cliente), la **carga útil recibida** y la **respuesta devuelta**, evidenciando el proceso de *marshalling/unmarshalling* del JSON en la capa de transporte.

## Evidencias de Ejecución

**Captura 1 — Invocación Exitosa:** petición `GET /productos` del cliente y respuesta correcta (200 + JSON) con deserialización de la lista:

<img width="1284" height="214" alt="Invocación exitosa TP7" src="capturas/invocacion_exitosa_tp7.png" />

**Captura 2 — Estructura de Datos (Payload):** detalle en la consola del servidor de la carga útil JSON recibida en el `POST /productos`:

<img width="563" height="106" alt="Estructura de datos TP7" src="capturas/estructura_datos_tp7.png" />

**Captura 3 — Manejo de Errores:** respuesta `404 Not Found` ante un producto inexistente y `400 Bad Request` ante un payload inválido:

<img width="752" height="340" alt="Manejo de errores TP7" src="capturas/manejo_errores_tp7.png" />

