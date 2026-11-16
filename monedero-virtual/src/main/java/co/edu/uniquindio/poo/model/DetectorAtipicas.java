package co.edu.uniquindio.poo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public final class DetectorAtipicas {

    public static final int MINIMO_HISTORIAL = 3;

    private DetectorAtipicas() {

    }

    public static double calcularPromedio(List<Transaccion> historial) {
        return historial.stream()
                .mapToDouble(Transaccion::getMonto)
                .average()
                .orElse(0);
    }


    public static double calcularDesviacion(List<Transaccion> historial) {
        double promedio = calcularPromedio(historial);
        double varianza = historial.stream()
                .mapToDouble(t -> Math.pow(t.getMonto() - promedio, 2))
                .average()
                .orElse(0);
        return Math.sqrt(varianza);
    }

    public static double calcularUmbral(List<Transaccion> historial) {
        return calcularPromedio(historial) + 2 * calcularDesviacion(historial);
    }

    public static boolean esAtipica(Transaccion transaccion, List<Transaccion> historial) {
        if (historial.size() < MINIMO_HISTORIAL) {
            return false;
        }
        return transaccion.getMonto() > calcularUmbral(historial);
    }


    public static List<Transaccion> detectarAtipicas(List<Transaccion> historial) {
        if (historial.size() < MINIMO_HISTORIAL) {
            return new ArrayList<>();
        }
        double umbral = calcularUmbral(historial);
        return historial.stream()
                .filter(t -> t.getMonto() > umbral)
                .collect(Collectors.toList());
    }
}
