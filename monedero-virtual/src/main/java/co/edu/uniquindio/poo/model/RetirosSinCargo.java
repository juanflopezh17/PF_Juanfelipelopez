package co.edu.uniquindio.poo.model;

import java.time.LocalDate;

public class RetirosSinCargo extends Beneficio {

    public static final int COSTO = 500;

    private LocalDate fechaVencimiento;

    public RetirosSinCargo() {
        super("Un mes sin cargos por retiros", COSTO);
    }

    @Override
    public void aplicar(Cliente cliente) {
        super.aplicar(cliente);
        this.fechaVencimiento = getFechaCanje().plusMonths(1);
    }

    @Override
    public double calcularDescuento(Transaccion transaccion) {
        if (estaVigente() && transaccion.getTipo() == TipoTransaccion.RETIRO) {
            return 1.0;
        }
        return 0;
    }

    @Override
    public boolean estaVigente() {
        return fechaVencimiento != null && !LocalDate.now().isAfter(fechaVencimiento);
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
}
