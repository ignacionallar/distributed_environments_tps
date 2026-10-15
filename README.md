# Desarrollo de Aplicaciones para Ambientes Distribuidos

**Profesor:** Lic. Gabriel Artaza
**Alumno:** Ignacio Eduardo Nallar

---

##  Descripción

En este repositorio se podrán encontrar todos los **trabajos prácticos realizados a lo largo de la cursada** de la materia *Desarrollo de Aplicaciones para Ambientes Distribuidos*.

###  Contenido

El repositorio contiene los diferentes trabajos prácticos, ejemplos, ejercicios y actividades desarrollados durante el trayecto de la cursada.

## Requisitos Previos

Antes de comenzar, asegúrate de tener instalado en tu sistema:
- **Git** (para clonar el repositorio).
- **Java Development Kit (JDK)** (versión 8 o superior, para compilar y ejecutar el código).

## Clonar repositorio

1. Abrir la terminal.
2. Posicionarse en la carpeta donde se desee clonar el repositorio (utilizando el comando cd <NOMBRE_DE_LA_CARPETA>).
3. Una vez posicionado en la carpeta deseada, colocar el siguiente comando:
git clone https://github.com/ignacionallar/distributed_environments_tps.git

<details>
<summary>TP 1 - Arquitectura Cliente-Servidor y Comunicación mediante Sockets TCP</summary>

## Instrucciones de ejecución

## Principal Folder: `Desarrollo/TP_1`

1. **Abrir terminal (Ctrl + Alt + Ñ)**
2. java ServidorCalculadora.java
3. **Abrir terminal (Ctrl + Alt + Ñ)**
4. java ClienteCalculadora.java

## Analisis Teorico-Practico
1. ¿Qué sucede con el cliente si el servidor no está ejecutándose al momento de
intentar conectar? Muestre la excepción que lanza Java.

    Si el servidor no está ejecutándose, no hay ningún proceso escuchando en el puerto 5500. El cliente intenta acceder, pero el sistema operativo local lo rechaza, lanzando la siguiente excepción:
    **java.net.ConnectException: Connection refused: connect**

3. Identifique en su código qué línea bloquea la ejecución del programa hasta que
ocurre un evento de red.

    En ServidorCalculadora.java
    Socket socket = serverSocket.accept(); 
    Bloquea la ejecución esperando un evento de conexión (hasta que un cliente intente conectarse).

    En ClienteCalculadora.java y ServidorCalculadora.java: 
    entrada.readLine(); 
    Bloquea la ejecución esperando un evento de recepción de datos (hasta que llegue una cadena de texto desde el otro extremo de la red).

4. Proponga qué cambios serían necesarios si dos compañeros de clase quisieran
ejecutar el Cliente en una notebook y el Servidor en otra conectadas al Wi-Fi del
aula.

    ClienteCalculadora.java
    Modificar la variable <String host = "127.0.0.1";> por la dirección IP privada de la notebook que actúa como servidor, por ejemplo: <String host = "192.168.1.45";>.

    Habilitar los permisos de Firewall necesarios de la notebook Servidor para recibir tráfico de la red.

## Evidencias
Se despliega una primera terminal, ejecutando java ServidorCalculadora.java, queda en standby esperando una conexion.
<img width="936" height="102" alt="image" src="https://github.com/user-attachments/assets/888107f1-bafc-4a1e-944b-8a065910f181" />

Se despliega una segunda termina, ejecutando java ClienteCalculadora.java, se envian los datos, los recibe y procesa el Servidor devolviendo un resultado.
<img width="870" height="161" alt="image" src="https://github.com/user-attachments/assets/4f383b31-d4dd-493f-ace0-3a740024c4de" />

Imagen del lado del Servidor en el momento de recepcion y proceso de paquete. Una vez enviado el paquete se cierra sesion.
<img width="872" height="152" alt="image" src="https://github.com/user-attachments/assets/23165508-c015-4f52-9656-1def951816af" />

</details>

<details>
<summary>TP 2 - Modelos Fundamentales (Comunicacion, Fallo y Seguridad) y Patrones de Resiliencia</summary>

## Instrucciones de ejecución

## Principal Folder: `Desarrollo/TP_2`

1. **Abrir terminal (Ctrl + Alt + Ñ)**
2. java ServidorInestable.java
3. **Abrir terminal (Ctrl + Alt + Ñ)**
4. java ClienteResiliente.java

## Analisis Teorico-Practico
1. ¿Qué problema genera en un servidor saturado que todos los clientes reintenten sus peticiones exactamente al mismo tiempo y con intervalos fijos (sin Jitter)? 
(Investigue el concepto de "Thundering Herd Problem" o "Efecto Estampida").

    En un sistema distribuido, si un servidor se encuentra saturado y cientos de clientes reintentan sus peticiones exactamente al mismo tiempo y con intervalos fijos (sin Jitter), se genera el problema conocido como **"Estampida de Peticiones"** o *Thundering Herd*. 
    * Esto provoca que los multiples clientes sobrecarguen simultaneamente al servidor que apenas intenta estabilizarse.
    * En lugar de lograr recuperarse, esta sincronizacion masiva de reintentos causa una nueva caida del sistema de forma inmediata. 
    * Al incorporar el **Jitter** (una variacion aleatoria de tiempo), los reintentos de todos los clientes se distribuyen a lo largo del tiempo, dándole al servidor la oportunidad y los recursos necesarios para recuperarse por completo.

2. ¿Qué diferencia existe entre un fallo transitorio y un fallo permanente? Dé un ejemplo de cada uno en una arquitectura distribuida.

    * **Fallo Transitorio:** Son fallas temporales que tienden a resolverse naturalmente por sí solas tras un breve periodo de tiempo. El patrón de reintento es ideal para estos casos, ya que un intento subsiguiente suele tener éxito una vez que la condición anómala desaparece. 
    * *Ejemplo:* Una interrupción momentánea de la red, un desbordamiento temporal de un buffer, o una sobrecarga momentánea en una base de datos distribuida.
    
    * **Fallo Permanente:** Son errores definitivos de los cuales el sistema no puede recuperarse por sí solo o de forma automática (a menudo clasificados en la teoría como *Crash-Stop* o Caída de Nodos). Reintentar repetidamente ante un fallo de este tipo solo desperdiciará recursos de red.
    * *Ejemplo:* Un servidor que repentinamente deja de responder y no se recupera debido a que se quemó un componente físico (falla de hardware) o se cortó un cable troncal.


## Evidencias
Estado inicial de ClienteResiliente.java sin deplegar el servidor.
<img width="967" height="359" alt="image" src="https://github.com/user-attachments/assets/af9b7712-7567-4985-9156-a4124533310b" />

Estado modificado con metricas ejecutando ClienteResiliente.java sin desplegar el servidor.
<img width="916" height="487" alt="image" src="https://github.com/user-attachments/assets/c3f35a40-ed29-4fbe-9119-22d9d7175bbd" />

Se despliega servidor.
<img width="933" height="80" alt="image" src="https://github.com/user-attachments/assets/29ac1b54-9ab0-4969-b93c-799367f05787" />

Resultado de ClienteResiliente.java con servidor desplegado.
<img width="878" height="404" alt="image" src="https://github.com/user-attachments/assets/8e673eaf-a6b9-42c2-b63b-ecf98776981c" />

Resultado de ServidorInestable.java con la conexion exitosa de ClienteResiliente.java
<img width="871" height="197" alt="image" src="https://github.com/user-attachments/assets/6eb179d5-5d89-4385-bb7a-1c035a968c54" />

</details>

<details>
<summary>TP 3 - Sockets TCP/UDP con API java.net y Servidores Multihilo</summary>

## Instrucciones de ejecución

## Principal Folder: `Desarrollo/TP_3`

1. **Abrir terminal (Ctrl + Alt + Ñ)**
2. `cd Serv_Chat_Multihilo`
3. `java ServidorTCP`
4. **Abrir terminales adicionales (Ctrl + Alt + Ñ)** para simular los clientes TCP:
5. `cd Serv_Chat_Multihilo`
6. `java ClienteTCP`
7. **Abrir terminal (Ctrl + Alt + Ñ)** para probar la resiliencia UDP:
8. `cd Socket_UDP`
8. `java ReceptorUDP`
9. **Abrir terminal (Ctrl + Alt + Ñ)**
10. `cd Socket_UDP`
11. `java EmisorUDP`

## Analisis Teorico-Practico

1. Capa de Transporte: Explique las diferencias estructurales y operativas entre una comunicación orientada a conexión (TCP) y una sin conexión (UDP) en el contexto del paso de mensajes.
    * **TCP (Orientado a conexión):** Estructuralmente, requiere establecer una sesión previa (Three-way handshake) entre cliente y servidor antes de transmitir datos. Operativamente, garantiza que los mensajes lleguen completos, sin errores y en el orden exacto en el que fueron enviados. Si un paquete se pierde en la red, TCP se encarga de retransmitirlo automáticamente. Es ideal para sistemas como el chat multihilo donde la integridad de la información es crítica.
    * **UDP (Sin conexión):** Estructuralmente, envía datagramas independientes sin verificar previamente si el receptor está listo o disponible. Operativamente, no garantiza la entrega, ni el orden, ni retransmite paquetes perdidos (Fire and Forget). Al tener menos sobrecarga de control, es mucho más rápido y ligero, siendo ideal para transmisiones periódicas como la telemetría o el streaming, donde perder un dato ocasional no corrompe el flujo general.

2. API java.net: ¿Cuál es la función del método ServerSocket.accept()? Justifique por qué es indispensable usar un hilo dedicado por cada cliente aceptado en un servidor TCP.
    * La función principal de `ServerSocket.accept()` es poner al servidor en un estado de escucha bloqueante. La ejecución del hilo actual se detiene en esa línea hasta que un cliente entrante solicita una conexión. Una vez concretada, el método devuelve un objeto `Socket` único que representa ese canal de comunicación bidireccional con el cliente.
    * Es indispensable delegar cada cliente a un hilo dedicado (Thread/Runnable) porque la lectura de flujos en Java (`in.readLine()`) también es bloqueante. Si un servidor atendiera a un cliente en su hilo principal, se quedaría atascado esperando sus mensajes y no podría volver a ejecutar el método `accept()` para recibir a nuevos usuarios, perdiendo su naturaleza concurrente.

3. Manejo de Errores y Resiliencia: ¿Qué sucede si el paquete UDP enviado por el emisor se pierde en la red? ¿Cómo lo detecta el receptor con setSoTimeout()?.
    * Debido a que UDP no implementa mecanismos de confirmación de recepción (ACK), si un datagrama se pierde en la red, el emisor no se entera del fallo y simplemente envía el siguiente paquete cuando le corresponda.
    * Para evitar que el servidor receptor se quede esperando un paquete perdido indefinidamente en el método `receive()`, se configura `socket.setSoTimeout(5000)`. Esto establece un temporizador interno. Si transcurren 5 segundos sin que llegue ningún datagrama a la cola del socket, la API interrumpe el bloqueo y lanza una `SocketTimeoutException`. Al capturar esta excepción (try-catch), el receptor detecta la anomalía en la red, registra la advertencia y puede continuar su ciclo normal de escucha sin quedar congelado.

## Evidencias
**EJERCICIO 1**
Ejecucion de ServidorTCP.java
<img width="928" height="302" alt="image" src="https://github.com/user-attachments/assets/d4e64400-04ae-45c4-842d-5451b6239957" />

Ejecucion de ClienteTCP.java y envio de mensjae.
<img width="914" height="223" alt="image" src="https://github.com/user-attachments/assets/a32a4005-a825-4f2b-bab3-3729e768e94c" />

Vista por parte de ServidorTCP.java
<img width="920" height="264" alt="image" src="https://github.com/user-attachments/assets/acc42e30-132d-4094-bcfc-17de751dd6f4" />

**EJERCICIO 2**
Ejecucion de EmisorUDP.java
<img width="925" height="233" alt="image" src="https://github.com/user-attachments/assets/e4983920-9409-4555-8685-695808edb015" />

Ejecucion de ReceptorUDP.java
<img width="921" height="235" alt="image" src="https://github.com/user-attachments/assets/0e65aa72-6144-4bb1-ae2a-686149b1e3f9" />

</details>

<details>
<summary>TP 4 - Representación de Datos y Paso de Mensajes</summary>

## Instrucciones de ejecución

## Principal Folder: `Desarrollo/TP_4`

1. **Abrir terminal (Ctrl + Alt + Ñ)**
2. java ServidorTCP.java
3. **Abrir terminal (Ctrl + Alt + Ñ)**
4. java ClienteTCP.java

## Análisis Teórico-Práctico

## Evidencias

Captura del servidor (`ServidorTCP.java`) recibiendo y deserializando la ráfaga de 1000 transacciones en ambos formatos:
<img width="584" height="252" alt="Servidor TCP TP4" src="Desarrollo/TP_4/capturas/servidor_tp4.png" />

Captura del cliente (`ClienteTCP.java`) mostrando el reporte de desempeño (JSON vs Binario):
<img width="665" height="330" alt="Cliente TCP TP4" src="Desarrollo/TP_4/capturas/cliente_tp4.png" />

</details>

<details>
<summary>TP 5 - Concurrencia y Sincronización con el Modelo de Actores</summary>

## Instrucciones de ejecución

## Principal Folder: `Desarrollo/TP_5`

1. **Abrir terminal (Ctrl + Alt + Ñ)**
2. java SistemaActores.java

## Analisis Teórico y Demostración de las 3 Operaciones

Este sistema evita completamente el uso de variables compartidas y primitivas de bloqueo (`synchronized`, `ReentrantLock`). La consistencia de los datos se logra canalizando todas las mutaciones a través de un único hilo (el Actor), demostrando las operaciones elementales:

1. **Crear (Spawn):** En el archivo `SistemaActores.java`, se instancian dinámicamente el `ActorProcesador` y múltiples instancias de `ActorSensor`, cada uno con su propio ciclo de vida e hilos.
2. **Enviar (Send):** Los actores sensores generan una ráfaga concurrente de 500 mensajes inmutables `MensajeLectura` y los envían al método `recibir()` del procesador. Este método simplemente encola (offer) los mensajes en una `ConcurrentLinkedQueue` (buzón FIFO) sin bloquear el hilo emisor.
3. **Designar (State Change):** Dentro del bucle continuo del `ActorProcesador` (su propio método `run()`), este desencola los mensajes uno a uno y procesa el contador y la sumatoria total. Al estar aislado en un único hilo, el estado interno muta de forma segura preparándose para el próximo mensaje sin generar condiciones de carrera ni necesidad de locks.

La finalización también se realiza por **paso de mensajes**: el coordinador encola un **mensaje centinela** (*poison pill*) para indicar al procesador que no hay más lecturas. De este modo no existe ninguna variable compartida entre hilos (ni siquiera un flag de control).

## Diagrama de Secuencia (Paso de Mensajes)

```mermaid
sequenceDiagram
    participant Sensor1 as ActorSensor 1
    participant SensorN as ActorSensor N
    participant Buzon as Mailbox (ConcurrentLinkedQueue)
    participant Procesador as ActorProcesador

    Procesador->>Procesador: 1. Crear (Spawn) e iniciar escucha
    Sensor1->>Sensor1: 1. Crear (Spawn)
    SensorN->>SensorN: 1. Crear (Spawn)

    rect rgb(200, 220, 250)
    Note over Sensor1, SensorN: Ráfaga Concurrente (500 Mensajes)
    Sensor1-->>Buzon: 2. Enviar (MensajeLectura A)
    SensorN-->>Buzon: 2. Enviar (MensajeLectura B)
    Sensor1-->>Buzon: 2. Enviar (MensajeLectura C)
    end

    loop Procesamiento Secuencial
        Procesador->>Buzon: Poll (Desencolar)
        Buzon-->>Procesador: Retorna MensajeLectura
        Procesador->>Procesador: 3. Designar / Cambio de Estado<br>(Actualiza contador y promedio)
    end
```

## Evidencias

Captura del inicio de la ráfaga concurrente y del comienzo del procesamiento secuencial de los mensajes en el buzón FIFO:

<img width="618" height="412" alt="Procesamiento TP5" src="Desarrollo/TP_5/capturas/procesamiento_tp5.png" />

Captura del final del procesamiento (mensajes N° 493 a 500) con el contador y el promedio final, evidenciando la ausencia de condiciones de carrera:

<img width="637" height="232" alt="Resumen TP5" src="Desarrollo/TP_5/capturas/resumen_tp5.png" />

</details>

<details>
<summary>TP 6 - Invocación Remota de Métodos (Java RMI)</summary>

## Instrucciones de ejecución

## Principal Folder: `Desarrollo/TP_6`

1. **Abrir terminal (Ctrl + Alt + Ñ)**
2. java ServidorProcesamiento.java
3. **Abrir terminal (Ctrl + Alt + Ñ)**
4. java ClienteProcesamiento.java

## Arquitectura


`ServicioRemoto` Interfaz remota (`extends Remote`) con los 3 métodos computacionales + la clase serializable `Estadisticas`. 
`ServidorProcesamiento` Publica el servicio en el registro RMI y delega la lógica de negocio en `ServicioRemotoImpl`. 
`ClienteProcesamiento` Obtiene la referencia remota vía `LocateRegistry`/`lookup` y presenta un menú interactivo.

## Ejercicio 2 - Cuestionario Teórico

### 1. Abstracción del Middleware
Con **sockets puros** el desarrollador debe gestionar manualmente la conexión, los puertos, el *framing* de los mensajes, la serialización/deserialización de los datos, la concurrencia y el manejo de errores de red. **Java RMI** abstrae todas esas tareas: el cliente invoca un método sobre un **Stub** local como si el objeto fuera local, y el middleware se encarga de la conexión, el *marshalling/unmarshalling*, la localización (registro de nombres) y la traducción de fallos de red a excepciones `RemoteException`. El desarrollador solo define la interfaz y la lógica de negocio.

### 2. Ciclo de Vida y Stub/Skeleton
- **Stub (cliente):** es un *proxy* que implementa la interfaz remota. Realiza el *marshalling* de los argumentos (los convierte a bytes), los envía por la red y, al recibir la respuesta, hace el *unmarshalling* del resultado. Si algo falla, lanza `RemoteException`.
- **Skeleton / Dispatcher (servidor):** recibe la petición, hace el *unmarshalling* de los argumentos, invoca el método real del objeto remoto y devuelve el resultado serializado. En RMI moderno el *skeleton* se genera dinámicamente (no se escribe a mano).

### 3. Manejo de Fallos Parciales
Si el servidor se interrumpe durante la ejecución de un método, el cliente recibe una excepción de la familia `RemoteException` (por ejemplo `ConnectException`, `ConnectIOException`, `UnmarshalException` o `NoSuchObjectException`). En producción deben capturarse de forma explícita, distinguir fallos transitorios (reintentables con *backoff*) de fallos permanentes, aplicar *timeouts* y degradar el servicio de forma controlada (no colgar la aplicación).

## Evidencias

Captura del servidor mostrando la publicación del servicio y la trazabilidad de cada invocación remota (host del cliente y operación ejecutada):

<img width="822" height="250" alt="Servidor RMI TP6" src="Desarrollo/TP_6/capturas/servidor_tp6.png" />

Captura del cliente mostrando el menú interactivo y los resultados de las tres operaciones remotas (estadísticas, validación de CUIT y filtro de contenido):

<img width="549" height="862" alt="Cliente RMI TP6" src="Desarrollo/TP_6/capturas/cliente_tp6.png" />

</details>

<details>
<summary>TP 7 - Integración entre Capas y Consumo de APIs / Servicios Distribuidos</summary>

## Instrucciones de ejecución

## Principal Folder: `Desarrollo/TP_7`

1. **Abrir terminal (Ctrl + Alt + Ñ)**
2. java servidor/ServidorAPI.java
3. **Abrir terminal (Ctrl + Alt + Ñ)**
4. java cliente/ClienteAPI.java

> Informe completo (carátula, arquitectura y evidencias): [`Desarrollo/TP_7/README.md`](Desarrollo/TP_7/README.md)

## Descripción

API REST de **Catálogo de Productos** implementada con `com.sun.net.httpserver.HttpServer` (servidor) y `java.net.http.HttpClient` (cliente), sin frameworks externos. Expone `GET /productos`, `GET /productos/{id}` y `POST /productos`, con manejo de códigos de estado (200, 201, 400, 404, 405, 500).

## Evidencias

Captura 1 — Invocación exitosa (`GET /productos` → 200 + JSON):

<img width="1284" height="214" alt="Invocación exitosa TP7" src="Desarrollo/TP_7/capturas/invocacion_exitosa_tp7.png" />

Captura 2 — Estructura de datos (payload JSON recibido en el servidor):

<img width="563" height="106" alt="Estructura de datos TP7" src="Desarrollo/TP_7/capturas/estructura_datos_tp7.png" />

Captura 3 — Manejo de errores (404 y 400):

<img width="752" height="340" alt="Manejo de errores TP7" src="Desarrollo/TP_7/capturas/manejo_errores_tp7.png" />

</details>
