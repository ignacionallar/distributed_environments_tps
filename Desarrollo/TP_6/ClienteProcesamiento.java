package TP_6;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class ClienteProcesamiento {

    private static final String HOST = "localhost";
    private static final int PUERTO = 1099;
    private static final String NOMBRE_SERVICIO = "ServicioProcesamiento";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PUERTO);
            ServicioRemoto servicio = (ServicioRemoto) registry.lookup(NOMBRE_SERVICIO);
            System.out.println("Conectado al servicio remoto '" + NOMBRE_SERVICIO + "'.");

            Scanner scanner = new Scanner(System.in);
            int opcion;
            do {
                mostrarMenu();
                opcion = Integer.parseInt(scanner.nextLine().trim());
                System.out.println();
                switch (opcion) {
                    case 1:
                        procesarNumeros(servicio, scanner);
                        break;
                    case 2:
                        validarCuit(servicio, scanner);
                        break;
                    case 3:
                        filtrarContenido(servicio, scanner);
                        break;
                    case 4:
                        System.out.println("Saliendo del cliente...");
                        break;
                    default:
                        System.out.println("Opcion invalida, intente nuevamente.");
                }
            } while (opcion != 4);

        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void mostrarMenu() {
        System.out.println("===== MENU - PROCESAMIENTO REMOTO =====");
        System.out.println("1. Estadisticas matematicas (promedio, maximo, minimo, desviacion estandar)");
        System.out.println("2. Validar CUIT");
        System.out.println("3. Filtrar contenido por patron");
        System.out.println("4. Salir");
        System.out.print("Elija una opcion: ");
    }

    private static void procesarNumeros(ServicioRemoto servicio, Scanner scanner) throws Exception {
        System.out.print("Ingrese los numeros separados por coma: ");
        String linea = scanner.nextLine().trim();
        System.out.println(linea);
        String[] partes = linea.split(",");
        double[] numeros = new double[partes.length];
        for (int i = 0; i < partes.length; i++) {
            numeros[i] = Double.parseDouble(partes[i].trim());
        }

        ServicioRemoto.Estadisticas e = servicio.procesarNumeros(numeros);
        System.out.println("Cantidad: " + e.getCantidad());
        System.out.println("Promedio: " + String.format("%.2f", e.getPromedio()));
        System.out.println("Maximo: " + String.format("%.2f", e.getMaximo()));
        System.out.println("Minimo: " + String.format("%.2f", e.getMinimo()));
        System.out.println("Desviacion estandar: " + String.format("%.2f", e.getDesviacionEstandar()));
    }

    private static void validarCuit(ServicioRemoto servicio, Scanner scanner) throws Exception {
        System.out.print("Ingrese el CUIT (formato XX-XXXXXXXX-X): ");
        String cuit = scanner.nextLine().trim();
        System.out.println(cuit);
        boolean valido = servicio.validarCuit(cuit);
        System.out.println("El CUIT ingresado es " + (valido ? "VALIDO" : "INVALIDO"));
    }

    private static void filtrarContenido(ServicioRemoto servicio, Scanner scanner) throws Exception {
        System.out.print("Ingrese las palabras separadas por coma: ");
        String lineaPalabras = scanner.nextLine().trim();
        System.out.println(lineaPalabras);
        String[] cadenas = lineaPalabras.split(",");
        for (int i = 0; i < cadenas.length; i++) {
            cadenas[i] = cadenas[i].trim();
        }

        System.out.print("Ingrese el patron de busqueda: ");
        String patron = scanner.nextLine().trim();
        System.out.println(patron);

        List<String> resultado = servicio.filtrarContenido(cadenas, patron);
        System.out.println("Palabras que coinciden con \"" + patron + "\": " + resultado);
    }
}
