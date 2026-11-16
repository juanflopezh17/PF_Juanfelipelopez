package co.edu.uniquindio.poo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Transaccion implements IPuntuable, IReversible {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String codigo;
    private LocalDateTime fecha;
    private double monto;
    private double comision;
    private int puntosGenerados;
    private EstadoTransaccion estado;
    private boolean sospechosa;
    private Monedero monederoOrigen;

    public Transaccion(String codigo, double monto, Monedero monederoOrigen) throws MontoInvalidoException {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de la transacción es obligatorio");
        }
        if (monto <= 0) {
            throw new MontoInvalidoException("El monto debe ser mayor que cero");
        }
        if (monederoOrigen == null) {
            throw new IllegalArgumentException("La transacción necesita un monedero");
        }
        this.codigo = codigo;
        this.monto = monto;
        this.monederoOrigen = monederoOrigen;
        this.fecha = LocalDateTime.now();
        this.comision = 0;
        this.puntosGenerados = 0;
        this.sospechosa = false;
        this.estado = EstadoTransaccion.EXITOSA;
    }


    public abstract void ejecutar() throws SaldoInsuficienteException;

    public abstract TipoTransaccion getTipo();


    public double calcularComisionBase() {
        return 0;
    }


    protected void validarReversible() throws TransaccionNoReversibleException {
        if (estaRevertida()) {
            throw new TransaccionNoReversibleException("La transacción " + codigo + " ya fue revertida");
        }
    }

    public void marcarSospechosa() {
        this.sospechosa = true;
    }

    public boolean estaRevertida() {
        return estado == EstadoTransaccion.REVERTIDA;
    }

    protected void marcarRevertida() {
        this.estado = EstadoTransaccion.REVERTIDA;
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    /** Permite fijar la fecha (útil para datos de prueba y para transacciones programadas). */
    public void setFecha(LocalDateTime fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        this.fecha = fecha;
    }

    public String getFechaTexto() {
        return fecha.format(FORMATO);
    }

    public double getMonto() {
        return monto;
    }

    public double getComision() {
        return comision;
    }

    public void setComision(double comision) {
        if (comision < 0) {
            throw new IllegalArgumentException("La comisión no puede ser negativa");
        }
        this.comision = comision;
    }

    public int getPuntosGenerados() {
        return puntosGenerados;
    }

    public void setPuntosGenerados(int puntosGenerados) {
        this.puntosGenerados = puntosGenerados;
    }

    public EstadoTransaccion getEstado() {
        return estado;
    }

    public boolean isSospechosa() {
        return sospechosa;
    }

    public Monedero getMonederoOrigen() {
        return monederoOrigen;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %.2f | %s%s", codigo, getFechaTexto(), getTipo(), monto, estado,
                sospechosa ? " | SOSPECHOSA" : "");
    }
}
