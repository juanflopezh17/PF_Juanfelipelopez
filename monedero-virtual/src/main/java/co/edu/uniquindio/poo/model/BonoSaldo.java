package co.edu.uniquindio.poo.model;

public class BonoSaldo extends Beneficio {

    public static final int COSTO = 1000;

    private double monto;
    private Monedero monederoDestino;

    public BonoSaldo(Monedero monederoDestino) {
        super("Bono de 50 en saldo", COSTO);
        if (monederoDestino == null) {
            throw new IllegalArgumentException("Debe elegir el monedero que recibe el bono");
        }
        this.monto = 50;
        this.monederoDestino = monederoDestino;
    }

    @Override
    public void aplicar(Cliente cliente) {
        if (monederoDestino.getPropietario() != cliente) {
            throw new IllegalArgumentException("El monedero del bono debe ser del mismo cliente");
        }
        super.aplicar(cliente);
        monederoDestino.aumentarSaldo(monto);
    }

    @Override
    public double calcularDescuento(Transaccion transaccion) {
        return 0; // no da descuento en comisiones
    }

    @Override
    public boolean estaVigente() {
        return false; // ya se usó al canjearlo
    }

    public double getMonto() {
        return monto;
    }

    public Monedero getMonederoDestino() {
        return monederoDestino;
    }

    @Override
    public String toString() {
        return getNombre() + " (" + COSTO + " pts) - abonado a " + monederoDestino.getNombre();
    }
}
