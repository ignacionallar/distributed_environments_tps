import java.io.*;
import java.net.*;

public class ServidorCalculadora {
    public static void main(String[] args) {
        int puerto = 5500; // Puerto solicitado en el TP
        
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("=== Servidor de Calculadora iniciado en el puerto " + puerto + " ===");
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

            // Procesar la solicitud matemática
            String respuesta;
            try {
                String[] partes = mensajeRecibido.split(";");
                int num1 = Integer.parseInt(partes[0]);
                String operacion = partes[1];
                int num2 = Integer.parseInt(partes[2]);

                switch (operacion) {
                    case "+": 
                        respuesta = String.valueOf(num1 + num2); 
                        break;
                    case "-": 
                        respuesta = String.valueOf(num1 - num2); 
                        break;
                    case "*": 
                        respuesta = String.valueOf(num1 * num2); 
                        break;
                    case "/": 
                        if (num2 == 0) {
                            respuesta = "ERROR: Division por cero";
                        } else {
                            respuesta = String.valueOf(num1 / num2);
                        }
                        break;
                    default: 
                        respuesta = "ERROR: Operación no válida";
                }
            } catch (Exception e) {
                respuesta = "ERROR: Formato incorrecto. Use 'numero;operacion;numero'";
            }

            // Enviar respuesta
            salida.println(respuesta);

            socket.close();
            System.out.println("Conexión cerrada.");

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}