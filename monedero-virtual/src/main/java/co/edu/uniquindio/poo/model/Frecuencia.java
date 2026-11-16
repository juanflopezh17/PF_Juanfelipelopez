package co.edu.uniquindio.poo.model;

import java.time.LocalDate;


    public enum Frecuencia {DIARIA, SEMANAL, MENSUAL;

    public LocalDate siguienteFecha(LocalDate fecha) {
        switch (this) {
            case DIARIA:
                return fecha.plusDays(1);
            case SEMANAL:
                return fecha.plusWeeks(1);
            default:
                return fecha.plusMonths(1);
        }
    }
}
