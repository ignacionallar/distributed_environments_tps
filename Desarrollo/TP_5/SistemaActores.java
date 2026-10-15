package TP_5;

public class SistemaActores {
    public static void main(String[] args) {
        // 1. Crear (Spawn): Instanciar dinámicamente el actor procesador con su buzón
        ActorProcesador procesador = new ActorProcesador();
        procesador.start();

        System.out.println("Iniciando envío de ráfaga concurrente...");

        // Crear (Spawn) 10 sensores que enviarán 50 mensajes cada uno simultáneamente (Total: 500 mensajes)
        int cantidadSensores = 10;
        Thread[] hilosSensores = new Thread[cantidadSensores];

        for (int i = 0; i < cantidadSensores; i++) {
            ActorSensor sensor = new ActorSensor(i + 1, procesador, 50);
            hilosSensores[i] = new Thread(sensor);
            // 2. Enviar (Send): Disparar la concurrencia
            hilosSensores[i].start(); 
        }

        // Esperar a que todos los sensores terminen de encolar sus mensajes
        for (Thread hilo : hilosSensores) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // La detención también se realiza por paso de mensajes (poison pill)
        procesador.recibirFin();
    }
}
