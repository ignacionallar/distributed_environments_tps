package TP_4;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClienteTCP {
    private static final String HOST = "localhost";
    private static final int PUERTO = 6000;
    private static final int TOTAL_TRANSACCIONES = 1000;

    public static void main(String[] args) {
        // Generar rafaga de 1000 transacciones simuladas
        List<Transaccion> transacciones = new ArrayList<>();
        for (int i = 1; i <= TOTAL_TRANSACCIONES; i++) {
            transacciones.add(new Transaccion(i, "NodoCliente", 1500.50 + i, System.currentTimeMillis()));
        }

        try (Socket socket = new Socket(HOST, PUERTO)) {
            System.out.println("Conectado al servidor. Iniciando transmisión de " + TOTAL_TRANSACCIONES + " transacciones...");

            // 1. ENVÍO EN JSON 
            PrintWriter outJson = new PrintWriter(socket.getOutputStream(), true);
            long bytesTotalesJson = 0;
            
            long inicioJson = System.currentTimeMillis();
            for (Transaccion t : transacciones) {
                String json = ParserMensajes.serializarAJson(t);
                byte[] jsonBytes = json.getBytes();
                bytesTotalesJson += jsonBytes.length;
                outJson.println(json); 
            }
            long tiempoTotalJson = System.currentTimeMillis() - inicioJson;

            // 2. ENVÍO EN BINARIO DIRECTO
            DataOutputStream outBinario = new DataOutputStream(socket.getOutputStream());
            long bytesTotalesBinario = 0;
            
            long inicioBinario = System.currentTimeMillis();
            for (Transaccion t : transacciones) {
                byte[] binarioBytes = ParserMensajes.serializarABinario(t);

                outBinario.writeInt(binarioBytes.length); 
                outBinario.write(binarioBytes);
                
                bytesTotalesBinario += 4 + binarioBytes.length; 
            }
            outBinario.flush();
            long tiempoTotalBinario = System.currentTimeMillis() - inicioBinario;

            // --- EVALUACIÓN DE DESEMPEÑO ---
            System.out.println("\n--- RESULTADOS EVALUACIÓN (OVERHEAD) ---");
            System.out.println("FORMATO JSON:");
            System.out.println("- Tamaño total de carga útil: " + bytesTotalesJson + " bytes.");
            System.out.println("- Tiempo total (serialización + envío): " + tiempoTotalJson + " ms.");
            
            System.out.println("\nFORMATO BINARIO:");
            System.out.println("- Tamaño total de carga útil: " + bytesTotalesBinario + " bytes.");
            System.out.println("- Tiempo total (serialización + envío): " + tiempoTotalBinario + " ms.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}