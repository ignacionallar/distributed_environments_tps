package TP_4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParserMensajes {

    // --- FORMATO TEXTO (JSON) ---
    public static String serializarAJson(Transaccion t) {
        // Serialización manual a JSON estandarizado
        return String.format("{\"id\":%d, \"origen\":\"%s\", \"monto\":%s, \"timestamp\":%d}",
                t.getIdTransaccion(), t.getOrigen(), String.valueOf(t.getMonto()), t.getTimestamp());
    }

    public static Transaccion deserializarDesdeJson(String json) {
        String limpio = json.replace("{", "").replace("}", "").replace("\"", "").trim();
        String[] partes = limpio.split(",");
        
        int id = Integer.parseInt(partes[0].split(":")[1].trim());
        String origen = partes[1].split(":")[1].trim();
        double monto = Double.parseDouble(partes[2].split(":")[1].trim());
        long timestamp = Long.parseLong(partes[3].split(":")[1].trim());
        
        return new Transaccion(id, origen, monto, timestamp);
    }

    // --- FORMATO BINARIO DIRECTO ---
    public static byte[] serializarABinario(Transaccion t) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        // Empacando tipos de datos directamente
        dos.writeInt(t.getIdTransaccion());
        dos.writeUTF(t.getOrigen());
        dos.writeDouble(t.getMonto());
        dos.writeLong(t.getTimestamp());
        
        dos.flush();
        return baos.toByteArray();
    }

    public static Transaccion deserializarDesdeBinario(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        
        int id = dis.readInt();
        String origen = dis.readUTF();
        double monto = dis.readDouble();
        long timestamp = dis.readLong();
        
        return new Transaccion(id, origen, monto, timestamp);
    }
}