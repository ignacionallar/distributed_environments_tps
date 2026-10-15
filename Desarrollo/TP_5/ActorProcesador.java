package TP_5;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ActorProcesador extends Thread {
    // Mensaje centinela (poison pill): detiene el actor sin compartir variables entre hilos
    private static final Object MENSAJE_FIN = new Object();

    // Buzón FIFO (Mailbox) lock-free, no requiere synchronized
    private final ConcurrentLinkedQueue<Object> buzon = new ConcurrentLinkedQueue<>();

    // Estado privado (Aislamiento de Estado)
    private int contadorHistorico = 0;
    private double sumaTotal = 0.0;

    // Operación: Enviar (Send) - Expuesto solo para recibir mensajes asincrónicos
    public void recibir(MensajeLectura mensaje) {
        buzon.offer(mensaje);
    }

    public void recibirFin() {
        buzon.offer(MENSAJE_FIN);
    }

    @Override
    public void run() {
        // Bucle de procesamiento del actor
        while (true) {
            Object mensaje = buzon.poll(); // Extraer del buzón

            if (mensaje == MENSAJE_FIN) {
                break;
            }

            if (mensaje instanceof MensajeLectura) {
                // Operación: Designar (State Change)
                // Se actualiza el estado interno para la atención del siguiente mensaje.
                // Como este código solo lo ejecuta este hilo, no hay condiciones de carrera.
                procesar((MensajeLectura) mensaje);
            } else {
                // Evita consumo excesivo de CPU cuando el buzón está vacío
                Thread.yield();
            }
        }
        System.out.println("=== PROCESAMIENTO FINALIZADO ===");
        System.out.println("Total procesado: " + contadorHistorico
                + " mensajes. Promedio final: " + (sumaTotal / contadorHistorico) + "°C");
    }

    private void procesar(MensajeLectura mensaje) {
        contadorHistorico++;
        sumaTotal += Math.round(mensaje.getValorTemperatura() * 100.0) / 100.0;
        double promedioActual = sumaTotal / contadorHistorico;
        System.out.printf("Procesando Msg N° %d | Temp: %.2f°C | Promedio Histórico: %.2f°C%n",
                contadorHistorico, mensaje.getValorTemperatura(), promedioActual);
    }
}
