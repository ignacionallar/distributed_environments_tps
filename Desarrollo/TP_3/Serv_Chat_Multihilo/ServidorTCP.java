package Serv_Chat_Multihilo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ServidorTCP {
    private static final int PUERTO = 5000;
    
    private static Set<PrintWriter> escritoresClientes = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Iniciando servidor de chat en el puerto " + PUERTO + "...");
        System.out.println("Escribí 'apagar' en esta consola y presioná Enter para detener el servidor.");
        
        Thread consolaAdmin = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (scanner.hasNextLine()) {
                    String comando = scanner.nextLine();
                    if ("apagar".equalsIgnoreCase(comando)) {
                        System.out.println("Apagando el servidor TCP de forma segura...");
                        System.exit(0); 
                    }
                }
            }
        });
        consolaAdmin.setDaemon(true);
        consolaAdmin.start();

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("Nuevo cliente conectado desde: " + socketCliente.getInetAddress());
                
                ManejadorCliente manejador = new ManejadorCliente(socketCliente);
                new Thread(manejador).start();
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    private static class ManejadorCliente implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (escritoresClientes) {
                    escritoresClientes.add(out);
                }

                String mensaje;
                while ((mensaje = in.readLine()) != null) {
                    System.out.println("Mensaje recibido para broadcast: " + mensaje);
                    difundirMensaje(mensaje);
                }
            } catch (IOException e) {
                System.out.println("Se interrumpió la conexión con el cliente.");
            } finally {
                if (out != null) {
                    synchronized (escritoresClientes) {
                        escritoresClientes.remove(out); 
                    }
                }
                try {
                    socket.close();
                    System.out.println("Cliente desconectado correctamente.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void difundirMensaje(String mensaje) {
            synchronized (escritoresClientes) {
                for (PrintWriter escritor : escritoresClientes) {
                    escritor.println(mensaje);
                }
            }
        }
    }
}