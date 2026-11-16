package co.edu.uniquindio.poo.model;

import java.time.LocalDateTime;

public class RegistroPuntos {

    private LocalDateTime fecha;
    private int puntos;
    private String descripcion;
    private String codigoTransaccion;

    public RegistroPuntos(int puntos, String descripcion, String codigoTransaccion) {
        this.fecha = LocalDateTime.now();
        this.puntos = puntos;
        this.descripcion = descripcion;
        this.codigoTransaccion = codigoTransaccion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public int getPuntos() {
        return puntos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCodigoTransaccion() {
        return codigoTransaccion;
    }

    @Override
    public String toString() {
        return (puntos > 0 ? "+" : "") + puntos + " pts - " + descripcion;
    }
}
