package Serv_Chat_Multihilo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteTCP {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PUERTO);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado exitosamente al chat en el puerto " + PUERTO);

            
            Thread hiloLectura = new Thread(() -> {
                try {
                    String mensajeServidor;
                    while ((mensajeServidor = in.readLine()) != null) {
                        System.out.println("\n[Chat] " + mensajeServidor);
                    }
                } catch (IOException e) {
                    System.out.println("\nConexión finalizada.");
                }
            });
    
            hiloLectura.setDaemon(true); 
            hiloLectura.start();

            System.out.println("Escribí un mensaje y presioná Enter (escribí 'salir' para abandonar):");
            
            while (true) {
                String mensaje = scanner.nextLine();
                
                if ("salir".equalsIgnoreCase(mensaje)) {
                    System.out.println("Cerrando el cliente...");
                    socket.close();
                    System.exit(0);
                }
                
                out.println(mensaje);
            }

        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }
}