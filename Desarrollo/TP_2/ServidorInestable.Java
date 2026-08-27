
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ServidorInestable {
    private static final int PUERTO = 5001;

    public static void main(String[] args) {
        System.out.println("=== Servidor Inestable Iniciado en puerto " + PUERTO + " ===");
        Random random = new Random();

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("\n[Servidor] Cliente conectado desde: " + clientSocket.getInetAddress());

                    int comportamiento = random.nextInt(100);

                    if (comportamiento < 40) {
                        // CASO 1: Simulación de congelamiento/latencia extrema (Timeout)
                        System.out.println("[Servidor ERROR] Simulando congelamiento (sleep 10s)...");
                        Thread.sleep(10000); 
                    } else if (comportamiento < 70) {
                        // CASO 2: Simulación de caída abrupta de conexión
                        System.out.println("[Servidor ERROR] Cierre abrupto de conexion sin responder.");
                        clientSocket.close();
                        continue;
                    }

                    // CASO 3: Respuesta exitosa
                    System.out.println("[Servidor OK] Procesando y respondiendo exitosamente.");
                    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                    out.writeUTF("OK: Procesamiento completado correctamente.");
                    out.flush();

                } catch (InterruptedException e) {
                    System.err.println("[Servidor Error] Hilo interrumpido.");
                } catch (IOException e) {
                    System.err.println("[Servidor Error] Error de E/S con el cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor: " + e.getMessage());
        }
    }
}