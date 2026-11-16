package co.edu.uniquindio.poo.model;

import java.time.LocalDate;

public class TransaccionProgramada implements Comparable<TransaccionProgramada> {

    private String codigo;
    private TipoTransaccion tipo;
    private double monto;
    private Monedero monederoOrigen;
    private Monedero monederoDestino;
    private LocalDate fechaEjecucion;
    private Frecuencia frecuencia;
    private boolean activa;

    public TransaccionProgramada(String codigo, TipoTransaccion tipo, double monto, Monedero monederoOrigen,
                                 Monedero monederoDestino, LocalDate fechaEjecucion, Frecuencia frecuencia)
            throws MontoInvalidoException {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (tipo == null || frecuencia == null || fechaEjecucion == null) {
            throw new IllegalArgumentException("Tipo, frecuencia y fecha son obligatorios");
        }
        if (monto <= 0) {
            throw new MontoInvalidoException("El monto programado debe ser mayor que cero");
        }
        if (monederoOrigen == null) {
            throw new IllegalArgumentException("Debe indicar el monedero");
        }
        if (tipo == TipoTransaccion.TRANSFERENCIA && (monederoDestino == null || monederoDestino == monederoOrigen)) {
            throw new IllegalArgumentException("La transferencia programada necesita un monedero destino diferente");
        }
        this.codigo = codigo;
        this.tipo = tipo;
        this.monto = monto;
        this.monederoOrigen = monederoOrigen;
        this.monederoDestino = tipo == TipoTransaccion.TRANSFERENCIA ? monederoDestino : null;
        this.fechaEjecucion = fechaEjecucion;
        this.frecuencia = frecuencia;
        this.activa = true;
    }

    public boolean estaPendiente(LocalDate hoy) {
        return activa && !fechaEjecucion.isAfter(hoy);
    }

    public Transaccion generarTransaccion(String codigoTransaccion) throws MontoInvalidoException {
        switch (tipo) {
            case DEPOSITO:
                return new Deposito(codigoTransaccion, monto, monederoOrigen);
            case RETIRO:
                return new Retiro(codigoTransaccion, monto, monederoOrigen);
            default:
                return new Transferencia(codigoTransaccion, monto, monederoOrigen, monederoDestino);
        }
    }

    public void reprogramar() {
        fechaEjecucion = frecuencia.siguienteFecha(fechaEjecucion);
    }

    @Override
    public int compareTo(TransaccionProgramada otra) {
        return this.fechaEjecucion.compareTo(otra.fechaEjecucion);
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }

    public Monedero getMonederoOrigen() {
        return monederoOrigen;
    }

    public Monedero getMonederoDestino() {
        return monederoDestino;
    }

    public LocalDate getFechaEjecucion() {
        return fechaEjecucion;
    }

    public Frecuencia getFrecuencia() {
        return frecuencia;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %.2f | %s | %s", codigo, tipo, monto, fechaEjecucion, frecuencia);
    }
}
