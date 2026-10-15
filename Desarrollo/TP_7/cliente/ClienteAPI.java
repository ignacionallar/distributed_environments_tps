package TP_7.cliente;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Scanner;

public class ClienteAPI {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;
        do {
            mostrarMenu();
            opcion = Integer.parseInt(scanner.nextLine().trim());
            System.out.println();
            switch (opcion) {
                case 1: listarProductos(); break;
                case 2: obtenerProducto(scanner); break;
                case 3: crearProducto(scanner); break;
                case 4: System.out.println("Saliendo del cliente..."); break;
                default: System.out.println("Opcion invalida, intente nuevamente.");
            }
        } while (opcion != 4);
    }

    private static void mostrarMenu() {
        System.out.println("===== CLIENTE - API CATALOGO DE PRODUCTOS =====");
        System.out.println("1. Listar productos (GET /productos)");
        System.out.println("2. Obtener producto por id (GET /productos/{id})");
        System.out.println("3. Crear producto (POST /productos)");
        System.out.println("4. Salir");
        System.out.print("Elija una opcion: ");
    }

    private static void listarProductos() {
        HttpResponse<String> resp = enviarGet("/productos");
        if (resp == null) { System.out.println("No se pudo conectar con el servidor."); return; }
        System.out.println("Estado HTTP: " + resp.statusCode());
        System.out.println("Respuesta (JSON): " + resp.body());
        System.out.println("Productos deserializados: " + contarOcurrencias(resp.body(), "\"id\""));
    }

    private static void obtenerProducto(Scanner scanner) {
        System.out.print("Ingrese el id del producto: ");
        String id = scanner.nextLine().trim();
        System.out.println(id);
        HttpResponse<String> resp = enviarGet("/productos/" + id);
        if (resp == null) { System.out.println("No se pudo conectar con el servidor."); return; }
        System.out.println("Estado HTTP: " + resp.statusCode());
        System.out.println("Respuesta (JSON): " + resp.body());
    }

    private static void crearProducto(Scanner scanner) {
        System.out.print("Ingrese el nombre del producto: ");
        String nombre = scanner.nextLine().trim();
        System.out.println(nombre);
        System.out.print("Ingrese el precio: ");
        String precio = scanner.nextLine().trim();
        System.out.println(precio);
        System.out.print("Ingrese el stock: ");
        String stock = scanner.nextLine().trim();
        System.out.println(stock);

        String payload = "{\"nombre\":\"" + nombre + "\",\"precio\":" + precio + ",\"stock\":" + stock + "}";
        System.out.println("Payload enviado (JSON): " + payload);

        HttpResponse<String> resp = enviarPost("/productos", payload);
        if (resp == null) { System.out.println("No se pudo conectar con el servidor."); return; }
        System.out.println("Estado HTTP: " + resp.statusCode());
        System.out.println("Respuesta (JSON): " + resp.body());
    }

    private static HttpResponse<String> enviarGet(String ruta) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + ruta))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            return http.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Error en GET " + ruta + ": " + e.getMessage());
            return null;
        }
    }

    private static HttpResponse<String> enviarPost(String ruta, String payload) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + ruta))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            return http.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Error en POST " + ruta + ": " + e.getMessage());
            return null;
        }
    }

    private static int contarOcurrencias(String texto, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = texto.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
}
