package TP_4;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTCP {
    private static final int PUERTO = 6000;
    private static final int TOTAL_TRANSACCIONES = 1000;

    public static void main(String[] args) {
        System.out.println("Servidor iniciado en el puerto " + PUERTO + ". Esperando peticiones...");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO);
             Socket cliente = serverSocket.accept()) {
             
            System.out.println("Cliente conectado: " + cliente.getInetAddress());

            // 1. Recepcion en formato JSON
            BufferedReader inJson = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            long bytesTotalesJson = 0;
            
            for (int i = 0; i < TOTAL_TRANSACCIONES; i++) {
                String lineaJson = inJson.readLine();
                bytesTotalesJson += lineaJson.getBytes().length;
                Transaccion t = ParserMensajes.deserializarDesdeJson(lineaJson);
            }
            System.out.println("--- RESULTADOS RECEPCIÓN SERVIDOR ---");
            System.out.println("JSON: 1000 transacciones recibidas y deserializadas.");
            System.out.println("JSON: Tamaño total recibido = " + bytesTotalesJson + " bytes.");

            // 2. Recepcion en formato Binario Directo
            DataInputStream inBinario = new DataInputStream(cliente.getInputStream());
            long bytesTotalesBinario = 0;
            
            for (int i = 0; i < TOTAL_TRANSACCIONES; i++) {
                // Leemos el tamaño del chunk binario primero
                int tamanioChunk = inBinario.readInt();
                bytesTotalesBinario += 4; // sumar los 4 bytes del entero que indica el tamaño
                
                byte[] buffer = new byte[tamanioChunk];
                inBinario.readFully(buffer);
                bytesTotalesBinario += tamanioChunk;
                
                Transaccion t = ParserMensajes.deserializarDesdeBinario(buffer);
            }
            System.out.println("BINARIO: 1000 transacciones recibidas y deserializadas.");
            System.out.println("BINARIO: Tamaño total recibido = " + bytesTotalesBinario + " bytes.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}