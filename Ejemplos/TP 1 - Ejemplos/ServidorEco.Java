import java.io.*;
import java.net.*;

public class ServidorEco {
    public static void main(String[] args) {
        int puerto = 5000;
        
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("=== Servidor iniciado en el puerto " + puerto + " ===");
            System.out.println("Esperando conexión de un cliente...");

            // La ejecución se bloquea en accept() hasta que se conecta un cliente
            Socket socket = serverSocket.accept();
            System.out.println("Cliente conectado desde: " + socket.getInetAddress().getHostAddress());

            // Canales de entrada y salida de datos
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            // Leer mensaje del cliente
            String mensajeRecibido = entrada.readLine();
            System.out.println("Mensaje recibido del cliente: " + mensajeRecibido);

            // Procesar y responder (convertir a mayúsculas)
            String respuesta = mensajeRecibido.toUpperCase();
            salida.println("RESPUESTA SERVIDOR: " + respuesta);

            socket.close();
            System.out.println("Conexión cerrada.");

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}