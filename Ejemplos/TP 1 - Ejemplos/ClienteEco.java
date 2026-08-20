import java.io.*;
import java.net.*;

public class ClienteEco {
    public static void main(String[] args) {
        String host = "127.0.0.1"; // Localhost
        int puerto = 5000;

        try (Socket socket = new Socket(host, puerto)) {
            System.out.println("Conectado exitosamente al servidor.");

            // Canales de entrada y salida
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Enviar mensaje al servidor
            String mensajeEnviar = "hola mundo desde el cliente distribuido";
            System.out.println("Enviando mensaje: " + mensajeEnviar);
            salida.println(mensajeEnviar);

            // Leer respuesta
            String respuesta = entrada.readLine();
            System.out.println("Respuesta del Servidor: " + respuesta);

        } catch (IOException e) {
            System.err.println("Error de conexión en el cliente: " + e.getMessage());
        }
    }
}