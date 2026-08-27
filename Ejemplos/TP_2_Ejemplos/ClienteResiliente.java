
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClienteResiliente {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5001;
    private static final int TIMEOUT_MS = 2000; // Timeout de 2 segundos
    private static final int MAX_REINTENTOS = 4;

    public static void main(String[] args) {
        System.out.println("=== Iniciando Cliente Resiliente ===");
        boolean exito = false;
        int intento = 0;
        int esperaMs = 1000; // Espera inicial de 1 segundo

        while (!exito && intento < MAX_REINTENTOS) {
            intento++;
            System.out.println("\n[Cliente] Intento " + intento + " de " + MAX_REINTENTOS + "...");

            try (Socket socket = new Socket()) {
                // Configurar Timeout de conexión y de lectura
                socket.connect(new InetSocketAddress(HOST, PUERTO), TIMEOUT_MS);
                socket.setSoTimeout(TIMEOUT_MS);

                DataInputStream in = new DataInputStream(socket.getInputStream());
                String respuesta = in.readUTF();

                System.out.println("[Cliente ÉXITO] Respuesta recibida: " + respuesta);
                exito = true;

            } catch (SocketTimeoutException e) {
                System.err.println("[Cliente ERROR] Timeout alcanzado (" + TIMEOUT_MS + " ms sin respuesta).");
            } catch (IOException e) {
                System.err.println("[Cliente ERROR] Error de comunicación/conexión: " + e.getMessage());
            }

            if (!exito && intento < MAX_REINTENTOS) {
                System.out.println("[Cliente] Aplicando Backoff Exponencial. Esperando " + esperaMs + " ms antes de reintentar...");
                try {
                    Thread.sleep(esperaMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                esperaMs *= 2; // Duplicar tiempo de espera para el siguiente intento
            }
        }

        if (!exito) {
            System.err.println("\n[Cliente FALLO DEFINITIVO] Se superó el máximo de reintentos. Operación cancelada.");
        }
    }
}