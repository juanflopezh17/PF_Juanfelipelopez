package co.edu.uniquindio.poo.model;

public class DescuentoTransferencia extends Beneficio {

    public static final int COSTO = 100;
    public static final int USOS = 5;

    private double porcentaje;
    private int usosRestantes;

    public DescuentoTransferencia() {
        super("Descuento 10% en transferencias", COSTO);
        this.porcentaje = 0.10;
        this.usosRestantes = USOS;
    }

    @Override
    public double calcularDescuento(Transaccion transaccion) {
        if (estaVigente() && transaccion.getTipo() == TipoTransaccion.TRANSFERENCIA) {
            return porcentaje;
        }
        return 0;
    }

    @Override
    public boolean estaVigente() {
        return usosRestantes > 0;
    }

    @Override
    public void registrarUso(Transaccion transaccion) {
        if (usosRestantes > 0) {
            usosRestantes--;
        }
    }

    public int getUsosRestantes() {
        return usosRestantes;
    }
}
