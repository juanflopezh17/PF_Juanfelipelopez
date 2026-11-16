package co.edu.uniquindio.poo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Monedero {

    public static final double SALDO_BAJO = 100;

    private String codigo;
    private String nombre;
    private TipoMonedero tipo;
    private double saldo;
    private Cliente propietario;
    private List<Transaccion> transacciones;

    public Monedero(String codigo, String nombre, TipoMonedero tipo, Cliente propietario) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del monedero es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del monedero es obligatorio");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de monedero es obligatorio");
        }
        if (propietario == null) {
            throw new IllegalArgumentException("El monedero debe tener un propietario");
        }
        this.codigo = codigo;
        this.nombre = nombre.trim();
        this.tipo = tipo;
        this.propietario = propietario;
        this.saldo = 0;
        this.transacciones = new ArrayList<>();
    }

    public void aumentarSaldo(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a sumar debe ser mayor que cero");
        }
        saldo += monto;
    }

    public void disminuirSaldo(double monto) throws SaldoInsuficienteException {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a descontar debe ser mayor que cero");
        }
        if (monto > saldo) {
            throw new SaldoInsuficienteException(String.format(
                    "Saldo insuficiente en el monedero '%s'. Disponible: %.2f, requerido: %.2f",
                    nombre, saldo, monto));
        }
        saldo -= monto;
    }

    public void registrarTransaccion(Transaccion transaccion) {
        transacciones.add(transaccion);
    }

    public boolean tieneSaldoBajo() {
        return saldo < SALDO_BAJO;
    }

    public List<Transaccion> filtrarPorTipo(TipoTransaccion tipo) {
        return transacciones.stream()
                .filter(t -> t.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoMonedero getTipo() {
        return tipo;
    }

    public double getSaldo() {
        return saldo;
    }

    public Cliente getPropietario() {
        return propietario;
    }

    public List<Transaccion> getTransacciones() {
        return Collections.unmodifiableList(transacciones);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + propietario.getNombre() + ")";
    }
}
