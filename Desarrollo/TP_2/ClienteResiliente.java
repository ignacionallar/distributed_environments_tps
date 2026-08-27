import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

public class ClienteResiliente {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5001;
    private static final int TIMEOUT_MS = 2000; // Timeout de 2 segundos
    private static final int MAX_REINTENTOS = 4;
    private static final int BASE_MS = 1000; // Base para el backoff exponencial

    public static void main(String[] args) {
        System.out.println("=== Iniciando Cliente Resiliente ===");
        boolean exito = false;
        int intento = 0;
        
        // Variables para Ejercicio 1 y 2
        Random random = new Random();
        long tiempoInicio = System.currentTimeMillis(); 

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
                // Ejercicio 1: Cálculo de Backoff Exponencial + Jitter
                long jitter = random.nextInt(501); // Random entre 0 y 500 ms
                long tiempoEsperado = (long) (BASE_MS * Math.pow(2, intento - 1)) + jitter;

                System.out.println("[Cliente] Aplicando Backoff Exponencial con Jitter. Esperando " + tiempoEsperado + " ms antes de reintentar...");
                try {
                    Thread.sleep(tiempoEsperado);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (!exito) {
            System.err.println("\n[Cliente FALLO DEFINITIVO] Se superó el máximo de reintentos. Operación cancelada.");
        }

        // Ejercicio 2: Impresión de Métricas de Resiliencia
        long tiempoTotal = System.currentTimeMillis() - tiempoInicio;
        
        System.out.println("\n==================================");
        System.out.println("      MÉTRICAS DE RESILIENCIA     ");
        System.out.println("==================================");
        System.out.println("Estado final de la petición: " + (exito ? "Éxito" : "Fallo definitivo"));
        System.out.println("Cantidad de intentos realizados: " + intento);
        System.out.println("Tiempo total transcurrido: " + tiempoTotal + " ms");
        System.out.println("==================================");
    }
}