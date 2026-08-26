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

