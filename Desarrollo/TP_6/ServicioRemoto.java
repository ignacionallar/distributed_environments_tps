package TP_6;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServicioRemoto extends Remote {

    Estadisticas procesarNumeros(double[] numeros) throws RemoteException;

    boolean validarCuit(String cuit) throws RemoteException;

    List<String> filtrarContenido(String[] cadenas, String patron) throws RemoteException;

    class Estadisticas implements Serializable {

        private static final long serialVersionUID = 1L;

        private final int cantidad;
        private final double promedio;
        private final double maximo;
        private final double minimo;
        private final double desviacionEstandar;

        public Estadisticas(int cantidad, double promedio, double maximo, double minimo, double desviacionEstandar) {
            this.cantidad = cantidad;
            this.promedio = promedio;
            this.maximo = maximo;
            this.minimo = minimo;
            this.desviacionEstandar = desviacionEstandar;
        }

        public int getCantidad() { return cantidad; }
        public double getPromedio() { return promedio; }
        public double getMaximo() { return maximo; }
        public double getMinimo() { return minimo; }
        public double getDesviacionEstandar() { return desviacionEstandar; }

        @Override
        public String toString() {
            return String.format("Estadisticas{cantidad=%d, promedio=%.2f, maximo=%.2f, minimo=%.2f, desviacionEstandar=%.2f}",
                    cantidad, promedio, maximo, minimo, desviacionEstandar);
        }
    }
}
