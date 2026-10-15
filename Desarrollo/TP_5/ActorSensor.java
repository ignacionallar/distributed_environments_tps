package TP_5;

public class ActorSensor implements Runnable {
    private final ActorProcesador procesador;
    private final int cantidadMensajes;
    private final int idSensor;

    public ActorSensor(int idSensor, ActorProcesador procesador, int cantidadMensajes) {
        this.idSensor = idSensor;
        this.procesador = procesador;
        this.cantidadMensajes = cantidadMensajes;
    }

    @Override
    public void run() {
        for (int i = 1; i <= cantidadMensajes; i++) {
            // Generar métrica aleatoria entre 20.0 y 35.0
            double temperatura = 20.0 + (Math.random() * 15.0); 
            MensajeLectura msj = new MensajeLectura(temperatura);
            
            // Paso de mensaje asincrónico al procesador
            procesador.recibir(msj);
        }
        System.out.println("Sensor " + idSensor + " terminó de enviar su ráfaga.");
    }
}
