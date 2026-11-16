package co.edu.uniquindio.poo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class Cliente extends Persona {

    private int puntosDisponibles;
    private int puntosAcumulados;
    private Rango rango;
    private List<Monedero> monederos;
    private List<RegistroPuntos> historialPuntos;
    private List<Beneficio> beneficios;
    private List<Notificacion> notificaciones;

    public Cliente(String nombre, String documento, String correo, String telefono) {
        super(nombre, documento, correo, telefono);
        this.puntosDisponibles = 0;
        this.puntosAcumulados = 0;
        this.rango = Rango.BRONCE;
        this.monederos = new ArrayList<>();
        this.historialPuntos = new ArrayList<>();
        this.beneficios = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
    }



    public void agregarMonedero(Monedero monedero) {
        if (monedero == null) {
            throw new IllegalArgumentException("El monedero no puede ser nulo");
        }
        if (buscarMonedero(monedero.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe un monedero con el código " + monedero.getCodigo());
        }
        monederos.add(monedero);
    }

    public Monedero buscarMonedero(String codigo) {
        return monederos.stream()
                .filter(m -> m.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
    }

    public double getSaldoTotal() {
        return monederos.stream().mapToDouble(Monedero::getSaldo).sum();
    }

    public List<Transaccion> obtenerTransacciones() {
        return monederos.stream()
                .flatMap(m -> m.getTransacciones().stream())
                .distinct()
                .sorted(Comparator.comparing(Transaccion::getFecha))
                .collect(Collectors.toList());
    }



    public void sumarPuntos(int puntos, String codigoTransaccion) {
        if (puntos <= 0) {
            return;
        }
        puntosDisponibles += puntos;
        puntosAcumulados += puntos;
        historialPuntos.add(new RegistroPuntos(puntos, "Puntos por transacción", codigoTransaccion));
    }

    public void restarPuntos(int puntos, String codigoTransaccion) {
        if (puntos <= 0) {
            return;
        }
        puntosDisponibles = Math.max(0, puntosDisponibles - puntos);
        puntosAcumulados = Math.max(0, puntosAcumulados - puntos);
        historialPuntos.add(new RegistroPuntos(-puntos, "Reversión de transacción", codigoTransaccion));
    }


    public boolean actualizarRango() {
        Rango nuevo = Rango.calcularRango(puntosAcumulados);
        boolean cambio = nuevo != rango;
        rango = nuevo;
        return cambio;
    }

    public void canjearBeneficio(Beneficio beneficio) throws PuntosInsuficientesException {
        if (beneficio == null) {
            throw new IllegalArgumentException("Debe seleccionar un beneficio");
        }
        if (puntosDisponibles < beneficio.getCostoPuntos()) {
            throw new PuntosInsuficientesException(String.format(
                    "Puntos insuficientes para '%s'. Tiene %d y necesita %d",
                    beneficio.getNombre(), puntosDisponibles, beneficio.getCostoPuntos()));
        }
        beneficio.aplicar(this);
        puntosDisponibles -= beneficio.getCostoPuntos();
        historialPuntos.add(new RegistroPuntos(-beneficio.getCostoPuntos(), "Canje: " + beneficio.getNombre(), "-"));
        beneficios.add(beneficio);
    }

    public double calcularComisionFinal(Transaccion transaccion) {
        double base = transaccion.calcularComisionBase();
        double descuentoBeneficio = buscarMejorBeneficio(transaccion)
                .map(b -> b.calcularDescuento(transaccion))
                .orElse(0.0);
        return base * (1 - rango.getDescuentoComision()) * (1 - descuentoBeneficio);
    }

    public void registrarUsoBeneficios(Transaccion transaccion) {
        buscarMejorBeneficio(transaccion).ifPresent(b -> b.registrarUso(transaccion));
    }

    private Optional<Beneficio> buscarMejorBeneficio(Transaccion transaccion) {
        return beneficios.stream()
                .filter(Beneficio::estaVigente)
                .filter(b -> b.calcularDescuento(transaccion) > 0)
                .max(Comparator.comparingDouble(b -> b.calcularDescuento(transaccion)));
    }

    public List<Beneficio> obtenerBeneficiosVigentes() {
        return beneficios.stream()
                .filter(Beneficio::estaVigente)
                .collect(Collectors.toList());
    }


    public void recibirNotificacion(Notificacion notificacion) {
        notificaciones.add(notificacion);
    }

    public int getPuntosDisponibles() {
        return puntosDisponibles;
    }

    public int getPuntosAcumulados() {
        return puntosAcumulados;
    }

    public Rango getRango() {
        return rango;
    }

    public List<Monedero> getMonederos() {
        return Collections.unmodifiableList(monederos);
    }

    public List<RegistroPuntos> getHistorialPuntos() {
        return Collections.unmodifiableList(historialPuntos);
    }

    public List<Beneficio> getBeneficios() {
        return Collections.unmodifiableList(beneficios);
    }

    public List<Notificacion> getNotificaciones() {
        return Collections.unmodifiableList(notificaciones);
    }
}
