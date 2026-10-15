package TP_5;

public class MensajeLectura {
    // El campo es final para garantizar su inmutabilidad
    private final double valorTemperatura; 

    public MensajeLectura(double valorTemperatura) {
        this.valorTemperatura = valorTemperatura;
    }

    public double getValorTemperatura() {
        return valorTemperatura;
    }
}
