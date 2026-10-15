package TP_7.servidor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ServidorAPI {

    private static final int PUERTO = 8080;
    private static final List<Producto> productos = new CopyOnWriteArrayList<>();
    private static final AtomicInteger contadorId = new AtomicInteger(4);

    public static void main(String[] args) throws IOException {
        productos.add(new Producto(1, "Notebook", 1500.0, 10));
        productos.add(new Producto(2, "Mouse", 25.5, 100));
        productos.add(new Producto(3, "Teclado", 45.0, 50));

        HttpServer server = HttpServer.create(new InetSocketAddress(PUERTO), 0);
        server.createContext("/productos", ServidorAPI::manejarProductos);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("API REST de Catalogo de Productos iniciada en http://localhost:" + PUERTO);
        System.out.println("  GET  /productos      -> listar productos");
        System.out.println("  GET  /productos/{id} -> obtener un producto");
        System.out.println("  POST /productos      -> crear un producto (JSON)");
        System.out.println("Servidor listo. Esperando peticiones HTTP...");
    }

    private static void manejarProductos(HttpExchange exchange) throws IOException {
        String metodo = exchange.getRequestMethod();
        String ruta = exchange.getRequestURI().getPath();
        String cliente = exchange.getRemoteAddress().getAddress().getHostAddress();
        System.out.println("[SERVIDOR] " + metodo + " " + ruta + " desde " + cliente);

        try {
            if ("/productos".equals(ruta)) {
                if ("GET".equals(metodo)) {
                    responder(exchange, 200, listarProductosJson());
                } else if ("POST".equals(metodo)) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("       Payload recibido -> " + body);
                    Producto nuevo = parsearProducto(body);
                    if (nuevo == null) {
                        responder(exchange, 400, "{\"error\":\"payload invalido. Formato: {\\\"nombre\\\":\\\"...\\\",\\\"precio\\\":0.0,\\\"stock\\\":0}\"}");
                    } else {
                        Producto creado = new Producto(contadorId.getAndIncrement(), nuevo.getNombre(), nuevo.getPrecio(), nuevo.getStock());
                        productos.add(creado);
                        responder(exchange, 201, creado.toJson());
                    }
                } else {
                    responder(exchange, 405, "{\"error\":\"metodo no permitido\"}");
                }
            } else if (ruta.startsWith("/productos/")) {
                if ("GET".equals(metodo)) {
                    int id = parsearId(ruta);
                    Producto p = buscarProducto(id);
                    if (p == null) {
                        responder(exchange, 404, "{\"error\":\"producto no encontrado\"}");
                    } else {
                        responder(exchange, 200, p.toJson());
                    }
                } else {
                    responder(exchange, 405, "{\"error\":\"metodo no permitido\"}");
                }
            } else {
                responder(exchange, 404, "{\"error\":\"recurso no encontrado\"}");
            }
        } catch (Exception e) {
            System.err.println("[SERVIDOR ERROR] " + e.getMessage());
            responder(exchange, 500, "{\"error\":\"error interno del servidor\"}");
        }
    }

    private static void responder(HttpExchange exchange, int codigo, String cuerpo) throws IOException {
        byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
        System.out.println("       Respuesta [" + codigo + "] -> " + cuerpo);
    }

    private static String listarProductosJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < productos.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(productos.get(i).toJson());
        }
        sb.append("]");
        return sb.toString();
    }

    private static Producto buscarProducto(int id) {
        for (Producto p : productos) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    private static int parsearId(String ruta) {
        String[] partes = ruta.split("/");
        return Integer.parseInt(partes[partes.length - 1]);
    }

    private static Producto parsearProducto(String json) {
        if (json == null || json.trim().isEmpty()) return null;
        try {
            String nombre = extraerTexto(json, "nombre");
            double precio = Double.parseDouble(extraerNumero(json, "precio"));
            int stock = Integer.parseInt(extraerNumero(json, "stock"));
            if (nombre == null || nombre.isEmpty() || precio < 0 || stock < 0) return null;
            return new Producto(0, nombre, precio, stock);
        } catch (Exception e) {
            return null;
        }
    }

    private static String extraerTexto(String json, String clave) {
        int idx = json.indexOf("\"" + clave + "\"");
        if (idx < 0) return null;
        int dosPuntos = json.indexOf(':', idx);
        int inicio = json.indexOf('"', dosPuntos + 1);
        int fin = json.indexOf('"', inicio + 1);
        if (inicio < 0 || fin < 0) return null;
        return json.substring(inicio + 1, fin);
    }

    private static String extraerNumero(String json, String clave) {
        int idx = json.indexOf("\"" + clave + "\"");
        if (idx < 0) return null;
        int inicio = json.indexOf(':', idx) + 1;
        int fin = inicio;
        while (fin < json.length()) {
            char c = json.charAt(fin);
            if (c == ',' || c == '}') break;
            fin++;
        }
        return json.substring(inicio, fin).trim();
    }

    static class Producto {
        private final int id;
        private final String nombre;
        private final double precio;
        private final int stock;

        Producto(int id, String nombre, double precio, int stock) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
            this.stock = stock;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public double getPrecio() { return precio; }
        public int getStock() { return stock; }

        public String toJson() {
            return "{\"id\":" + id + ",\"nombre\":\"" + nombre + "\",\"precio\":" + precio + ",\"stock\":" + stock + "}";
        }
    }
}
