package co.edu.uniquindio.poo.model;

public class Deposito extends Transaccion {

    public Deposito(String codigo, double monto, Monedero monedero) throws MontoInvalidoException {
        super(codigo, monto, monedero);
    }

    @Override
    public void ejecutar() {
        getMonederoOrigen().aumentarSaldo(getMonto());
        getMonederoOrigen().registrarTransaccion(this);
    }

    @Override
    public int calcularPuntos() {
        return (int) (getMonto() / 100) * 1;
    }

    @Override
    public void revertir() throws TransaccionNoReversibleException {
        validarReversible();
        try {
            getMonederoOrigen().disminuirSaldo(getMonto());
        } catch (SaldoInsuficienteException e) {
            throw new TransaccionNoReversibleException(
                    "No se puede revertir el depósito " + getCodigo() + ": el dinero ya fue usado");
        }
        marcarRevertida();
    }

    @Override
    public TipoTransaccion getTipo() {
        return TipoTransaccion.DEPOSITO;
    }
}
