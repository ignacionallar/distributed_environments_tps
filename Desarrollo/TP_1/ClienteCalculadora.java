import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteCalculadora {
    public static void main(String[] args) {
        String host = "127.0.0.1"; // Localhost
        int puerto = 5500;

        try (Socket socket = new Socket(host, puerto)) {
            System.out.println("Conectado exitosamente al servidor.");

            // Canales de entrada y salida
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Solicitar datos al usuario por consola
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese el primer número entero: ");
            String num1 = scanner.nextLine();
            
            System.out.print("Ingrese la operación (+, -, *, /): ");
            String operacion = scanner.nextLine();
            
            System.out.print("Ingrese el segundo número entero: ");
            String num2 = scanner.nextLine();

            // Empaquetar mensaje y enviar al servidor
            String mensajeEnviar = num1 + ";" + operacion + ";" + num2;
            System.out.println("Enviando mensaje: " + mensajeEnviar);
            salida.println(mensajeEnviar);

            // Leer respuesta
            String respuesta = entrada.readLine();
            System.out.println("Respuesta del Servidor (Resultado): " + respuesta);

        } catch (IOException e) {
            System.err.println("Error de conexión en el cliente: " + e.getMessage());
        }
    }
}