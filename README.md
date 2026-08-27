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

# TP 1 - Arquitectura Cliente-Servidor y Comunicación mediante Sockets TCP

## Instrucciones de ejecución

## Principal Folder: ../Desarrollo/TP_1

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


# TP 2 - Modelos Fundamentales (Comunicacion, Fallo y Seguridad) y Patrones de Resiliencia.

## Instrucciones de ejecución

## Principal Folder: ../Desarrollo/TP_2

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
