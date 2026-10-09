package Socket_UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class ReceptorUDP {
    public static void main(String[] args) {
        int puerto = 5005;
        byte[] buffer = new byte[1024];

        try (DatagramSocket socket = new DatagramSocket(puerto)) {
            socket.setSoTimeout(5000);
            System.out.println("Receptor UDP iniciado en el puerto " + puerto + ".");
            System.out.println("Esperando alertas de telemetría...");

            while (true) {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(paquete);
                    
                    String mensaje = new String(paquete.getData(), 0, paquete.getLength());
                    System.out.println("Alerta recibida desde " + paquete.getAddress().getHostAddress() + ":" + paquete.getPort() + " -> " + mensaje);
                } catch (SocketTimeoutException e) {
                    System.out.println("Advertencia: No se recibió ningún datagrama en los últimos 5 segundos. Continuando la escucha...");
                }
            }
        } catch (IOException e) {
            System.err.println("Error en el receptor UDP: " + e.getMessage());
        }
    }
}