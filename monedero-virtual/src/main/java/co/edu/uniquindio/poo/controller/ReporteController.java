package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.DetectorAtipicas;
import co.edu.uniquindio.poo.model.RegistroNoEncontradoException;
import co.edu.uniquindio.poo.model.SistemaMonedero;
import co.edu.uniquindio.poo.model.Transaccion;

import java.util.List;
import java.util.stream.Collectors;


public class ReporteController {

    private final SistemaMonedero sistema;

    public ReporteController(SistemaMonedero sistema) {
        this.sistema = sistema;
    }

    public List<Cliente> obtenerClientes() {
        return sistema.getClientes();
    }

    public String generarReporte(Cliente cliente) throws RegistroNoEncontradoException {
        if (cliente == null) {
            throw new IllegalArgumentException("Seleccione un cliente");
        }
        return sistema.generarReporte(cliente.getDocumento());
    }

    public String analizarAtipicas(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Seleccione un cliente");
        }
        List<Transaccion> historial = cliente.obtenerTransacciones().stream()
                .filter(t -> !t.estaRevertida())
                .collect(Collectors.toList());
        if (historial.size() < DetectorAtipicas.MINIMO_HISTORIAL) {
            return "Se necesitan al menos " + DetectorAtipicas.MINIMO_HISTORIAL
                    + " transacciones para el análisis.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Promedio: %.2f%n", DetectorAtipicas.calcularPromedio(historial)));
        sb.append(String.format("Desviación estándar: %.2f%n", DetectorAtipicas.calcularDesviacion(historial)));
        sb.append(String.format("Umbral (promedio + 2σ): %.2f%n%n", DetectorAtipicas.calcularUmbral(historial)));
        List<Transaccion> atipicas = DetectorAtipicas.detectarAtipicas(historial);
        sb.append("Transacciones por encima del umbral: ").append(atipicas.size()).append("\n");
        atipicas.forEach(t -> sb.append("  ").append(t).append("\n"));
        return sb.toString();
    }
}
