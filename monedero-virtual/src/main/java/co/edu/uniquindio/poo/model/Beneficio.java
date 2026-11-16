package co.edu.uniquindio.poo.model;

import java.time.LocalDate;


public abstract class Beneficio {

    private String nombre;
    private int costoPuntos;
    private LocalDate fechaCanje;

    public Beneficio(String nombre, int costoPuntos) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del beneficio es obligatorio");
        }
        if (costoPuntos <= 0) {
            throw new IllegalArgumentException("El costo en puntos debe ser mayor que cero");
        }
        this.nombre = nombre;
        this.costoPuntos = costoPuntos;
    }


    public void aplicar(Cliente cliente) {
        this.fechaCanje = LocalDate.now();
    }


    public abstract double calcularDescuento(Transaccion transaccion);

    public abstract boolean estaVigente();


    public void registrarUso(Transaccion transaccion) {
    }

    public String getNombre() {
        return nombre;
    }

    public int getCostoPuntos() {
        return costoPuntos;
    }

    public LocalDate getFechaCanje() {
        return fechaCanje;
    }

    @Override
    public String toString() {
        return nombre + " (" + costoPuntos + " pts)" + (estaVigente() ? " - vigente" : " - vencido");
    }
}
