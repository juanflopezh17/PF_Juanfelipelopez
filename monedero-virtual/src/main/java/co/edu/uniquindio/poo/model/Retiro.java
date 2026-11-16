package co.edu.uniquindio.poo.model;

public class Retiro extends Transaccion {

    public static final double COMISION = 0.01;

    public Retiro(String codigo, double monto, Monedero monedero) throws MontoInvalidoException {
        super(codigo, monto, monedero);
    }

    @Override
    public void ejecutar() throws SaldoInsuficienteException {
        getMonederoOrigen().disminuirSaldo(getMonto() + getComision());
        getMonederoOrigen().registrarTransaccion(this);
    }

    @Override
    public double calcularComisionBase() {
        return getMonto() * COMISION;
    }

    @Override
    public int calcularPuntos() {
        return (int) (getMonto() / 100) * 2;
    }

    @Override
    public void revertir() throws TransaccionNoReversibleException {
        validarReversible();
        getMonederoOrigen().aumentarSaldo(getMonto() + getComision());
        marcarRevertida();
    }

    @Override
    public TipoTransaccion getTipo() {
        return TipoTransaccion.RETIRO;
    }
}
