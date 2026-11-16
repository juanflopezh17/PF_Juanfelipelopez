package co.edu.uniquindio.poo.model;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


public final class AnalizadorGastos {

    private AnalizadorGastos() {

    }


    private static List<Transaccion> movimientosPropios(Cliente cliente) {
        return cliente.obtenerTransacciones().stream()
                .filter(t -> !t.estaRevertida())
                .filter(t -> t.getMonederoOrigen().getPropietario() == cliente)
                .collect(Collectors.toList());
    }

    private static List<Transaccion> gastos(Cliente cliente) {
        return movimientosPropios(cliente).stream()
                .filter(t -> t.getTipo() != TipoTransaccion.DEPOSITO)
                .collect(Collectors.toList());
    }

    public static Map<TipoTransaccion, Double> totalPorTipo(Cliente cliente) {
        return movimientosPropios(cliente).stream()
                .collect(Collectors.groupingBy(Transaccion::getTipo,
                        Collectors.summingDouble(Transaccion::getMonto)));
    }

    public static Map<YearMonth, Double> gastosPorMes(Cliente cliente) {
        return gastos(cliente).stream()
                .collect(Collectors.groupingBy(t -> YearMonth.from(t.getFecha()),
                        TreeMap::new,
                        Collectors.summingDouble(Transaccion::getMonto)));
    }

    public static Map<String, Double> gastosPorMonedero(Cliente cliente) {
        return gastos(cliente).stream()
                .collect(Collectors.groupingBy(t -> t.getMonederoOrigen().getNombre(),
                        Collectors.summingDouble(Transaccion::getMonto)));
    }

    public static List<Transaccion> mayoresGastos(Cliente cliente, int cantidad) {
        return gastos(cliente).stream()
                .sorted(Comparator.comparingDouble(Transaccion::getMonto).reversed())
                .limit(cantidad)
                .collect(Collectors.toList());
    }

    public static double promedioGasto(Cliente cliente) {
        return gastos(cliente).stream()
                .mapToDouble(Transaccion::getMonto)
                .average()
                .orElse(0);
    }

    public static double totalComisiones(Cliente cliente) {
        return gastos(cliente).stream()
                .mapToDouble(Transaccion::getComision)
                .sum();
    }

    public static String generarReporte(Cliente cliente) {
        StringBuilder sb = new StringBuilder();
        sb.append("REPORTE DE GASTOS - ").append(cliente.getNombre()).append("\n");
        sb.append(String.format("Rango: %s | Puntos acumulados: %d | Disponibles: %d%n",
                cliente.getRango(), cliente.getPuntosAcumulados(), cliente.getPuntosDisponibles()));
        sb.append(String.format("Saldo total: %.2f%n%n", cliente.getSaldoTotal()));

        sb.append("Total por tipo de transacción:\n");
        totalPorTipo(cliente).forEach((tipo, total) ->
                sb.append(String.format("  %-14s %10.2f%n", tipo, total)));

        sb.append("\nGastos por mes:\n");
        gastosPorMes(cliente).forEach((mes, total) ->
                sb.append(String.format("  %-14s %10.2f%n", mes, total)));

        sb.append("\nGastos por monedero:\n");
        gastosPorMonedero(cliente).forEach((monedero, total) ->
                sb.append(String.format("  %-14s %10.2f%n", monedero, total)));

        sb.append(String.format("%nGasto promedio: %.2f%n", promedioGasto(cliente)));
        sb.append(String.format("Comisiones pagadas: %.2f%n", totalComisiones(cliente)));

        sb.append("\nLos 3 mayores gastos:\n");
        mayoresGastos(cliente, 3).forEach(t -> sb.append("  ").append(t).append("\n"));

        List<Transaccion> sospechosas = cliente.obtenerTransacciones().stream()
                .filter(Transaccion::isSospechosa)
                .collect(Collectors.toList());
        sb.append("\nTransacciones marcadas como sospechosas: ").append(sospechosas.size()).append("\n");
        sospechosas.forEach(t -> sb.append("  ").append(t).append("\n"));
        return sb.toString();
    }
}
