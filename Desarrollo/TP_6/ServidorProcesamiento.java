package TP_6;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServidorProcesamiento {

    private static final int PUERTO = 1099;
    private static final String NOMBRE_SERVICIO = "ServicioProcesamiento";

    public static void main(String[] args) {
        try {
            ServicioRemotoImpl servicio = new ServicioRemotoImpl();

            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PUERTO);
                System.out.println("Registro RMI creado en el puerto " + PUERTO + ".");
            } catch (RemoteException e) {
                registry = LocateRegistry.getRegistry(PUERTO);
                System.out.println("Registro RMI ya existente, reutilizando el puerto " + PUERTO + ".");
            }

            registry.rebind(NOMBRE_SERVICIO, servicio);
            System.out.println("Servicio remoto '" + NOMBRE_SERVICIO + "' publicado.");
            System.out.println("Servidor listo. Esperando invocaciones remotas...");

        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class ServicioRemotoImpl extends UnicastRemoteObject implements ServicioRemoto {

    private static final long serialVersionUID = 1L;

    protected ServicioRemotoImpl() throws RemoteException {
        super();
    }

    @Override
    public ServicioRemoto.Estadisticas procesarNumeros(double[] numeros) throws RemoteException {
        System.out.println("[SERVIDOR] Cliente " + clienteActual() + " -> procesarNumeros(" + Arrays.toString(numeros) + ")");

        if (numeros == null || numeros.length == 0) {
            return new ServicioRemoto.Estadisticas(0, 0.0, 0.0, 0.0, 0.0);
        }

        int cantidad = numeros.length;
        double suma = 0.0;
        double maximo = Double.NEGATIVE_INFINITY;
        double minimo = Double.POSITIVE_INFINITY;
        for (double n : numeros) {
            suma += n;
            if (n > maximo) maximo = n;
            if (n < minimo) minimo = n;
        }
        double promedio = suma / cantidad;

        double sumaCuadrados = 0.0;
        for (double n : numeros) {
            sumaCuadrados += Math.pow(n - promedio, 2);
        }
        double desviacion = Math.sqrt(sumaCuadrados / cantidad);

        ServicioRemoto.Estadisticas resultado = new ServicioRemoto.Estadisticas(cantidad, promedio, maximo, minimo, desviacion);
        System.out.println("       Resultado -> " + resultado);
        return resultado;
    }

    @Override
    public boolean validarCuit(String cuit) throws RemoteException {
        System.out.println("[SERVIDOR] Cliente " + clienteActual() + " -> validarCuit(\"" + cuit + "\")");

        String limpio = cuit == null ? "" : cuit.replaceAll("[^0-9]", "");
        boolean valido = false;
        if (limpio.length() == 11) {
            int[] factores = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
            int suma = 0;
            for (int i = 0; i < 10; i++) {
                suma += (limpio.charAt(i) - '0') * factores[i];
            }
            int verificador = 11 - (suma % 11);
            if (verificador == 11) {
                verificador = 0;
            }
            valido = verificador == (limpio.charAt(10) - '0');
        }
        System.out.println("       Resultado -> " + (valido ? "valido" : "invalido"));
        return valido;
    }

    @Override
    public List<String> filtrarContenido(String[] cadenas, String patron) throws RemoteException {
        System.out.println("[SERVIDOR] Cliente " + clienteActual() + " -> filtrarContenido(" + Arrays.toString(cadenas) + ", \"" + patron + "\")");

        List<String> resultado = new ArrayList<>();
        if (cadenas != null && patron != null) {
            for (String c : cadenas) {
                if (c != null && c.toLowerCase().contains(patron.toLowerCase())) {
                    resultado.add(c);
                }
            }
        }
        System.out.println("       Resultado -> " + resultado);
        return resultado;
    }

    private String clienteActual() {
        try {
            return RemoteServer.getClientHost();
        } catch (ServerNotActiveException e) {
            return "desconocido";
        }
    }
}
