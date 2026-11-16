package co.edu.uniquindio.poo.model;

import java.util.Arrays;

public enum Rango {

    BRONCE(0, 500, 0.0, 0.0),
    PLATA(501, 1000, 0.05, 0.05),
    ORO(1001, 5000, 0.10, 0.10),
    PLATINO(5001, Integer.MAX_VALUE, 0.20, 0.15);

    private final int puntosMin;
    private final int puntosMax;
    private final double descuentoComision;
    private final double bonoPuntos;

    Rango(int puntosMin, int puntosMax, double descuentoComision, double bonoPuntos) {
        this.puntosMin = puntosMin;
        this.puntosMax = puntosMax;
        this.descuentoComision = descuentoComision;
        this.bonoPuntos = bonoPuntos;
    }

    public static Rango calcularRango(int puntos) {
        return Arrays.stream(values())
                .filter(r -> puntos >= r.puntosMin && puntos <= r.puntosMax)
                .findFirst()
                .orElse(BRONCE);
    }

    public int aplicarBono(int puntos) {
        return puntos + (int) Math.round(puntos * bonoPuntos);
    }

    public int getPuntosMin() {
        return puntosMin;
    }

    public int getPuntosMax() {
        return puntosMax;
    }

    public double getDescuentoComision() {
        return descuentoComision;
    }

    public double getBonoPuntos() {
        return bonoPuntos;
    }
}
