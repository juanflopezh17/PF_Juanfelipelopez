package co.edu.uniquindio.poo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Notificacion {

    private LocalDateTime fecha;
    private String mensaje;
    private TipoAlerta tipo;
    private String canal;

    public Notificacion(String mensaje, TipoAlerta tipo, String canal) {
        if (mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException("El mensaje es obligatorio");
        }
        this.fecha = LocalDateTime.now();
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.canal = canal;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public TipoAlerta getTipo() {
        return tipo;
    }

    public String getCanal() {
        return canal;
    }

    @Override
    public String toString() {
        return "[" + fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "] " + tipo + ": " + mensaje;
    }
}
