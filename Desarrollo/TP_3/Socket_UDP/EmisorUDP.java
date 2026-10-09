package Socket_UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class EmisorUDP {
    public static void main(String[] args) {
        String hostDestino = "localhost";
        int puertoDestino = 5005;
        
        int intervaloEnvio = 3000; 

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress direccionReceptor = InetAddress.getByName(hostDestino);
            int contador = 1;

            System.out.println("Iniciando emisor de telemetría UDP hacia " + hostDestino + ":" + puertoDestino);

            while (true) {
                String mensaje = "Alerta crítica de telemetría N° " + contador;
                byte[] buffer = mensaje.getBytes();

                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, direccionReceptor, puertoDestino);
                socket.send(paquete);
                System.out.println("Enviado: " + mensaje);

                contador++;
                
                Thread.sleep(intervaloEnvio);
            }
        } catch (Exception e) {
            System.err.println("Error en el emisor UDP: " + e.getMessage());
        }
    }
}