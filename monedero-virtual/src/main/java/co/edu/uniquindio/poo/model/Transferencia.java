package co.edu.uniquindio.poo.model;

public class Transferencia extends Transaccion {

    public static final double COMISION = 0.02;

    private Monedero monederoDestino;

    public Transferencia(String codigo, double monto, Monedero origen, Monedero destino)
            throws MontoInvalidoException {
        super(codigo, monto, origen);
        if (destino == null) {
            throw new IllegalArgumentException("La transferencia necesita un monedero destino");
        }
        if (destino == origen) {
            throw new IllegalArgumentException("El monedero destino debe ser diferente al de origen");
        }
        this.monederoDestino = destino;
    }

    @Override
    public void ejecutar() throws SaldoInsuficienteException {
        getMonederoOrigen().disminuirSaldo(getMonto() + getComision());
        monederoDestino.aumentarSaldo(getMonto());
        getMonederoOrigen().registrarTransaccion(this);
        monederoDestino.registrarTransaccion(this);
    }

    @Override
    public double calcularComisionBase() {
        return getMonto() * COMISION;
    }

    @Override
    public int calcularPuntos() {
        return (int) (getMonto() / 100) * 3;
    }

    @Override
    public void revertir() throws TransaccionNoReversibleException {
        validarReversible();
        try {
            monederoDestino.disminuirSaldo(getMonto());
        } catch (SaldoInsuficienteException e) {
            throw new TransaccionNoReversibleException(
                    "No se puede revertir la transferencia " + getCodigo() + ": el destino ya usó el dinero");
        }
        getMonederoOrigen().aumentarSaldo(getMonto() + getComision());
        marcarRevertida();
    }

    @Override
    public TipoTransaccion getTipo() {
        return TipoTransaccion.TRANSFERENCIA;
    }

    public Monedero getMonederoDestino() {
        return monederoDestino;
    }
}
