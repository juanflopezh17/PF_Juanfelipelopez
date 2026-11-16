package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.model.MontoInvalidoException;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.RegistroNoEncontradoException;
import co.edu.uniquindio.poo.model.SaldoInsuficienteException;
import co.edu.uniquindio.poo.model.SistemaMonedero;
import co.edu.uniquindio.poo.model.TipoTransaccion;
import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.model.TransaccionNoReversibleException;

import java.util.List;

public class TransaccionController {

    private final SistemaMonedero sistema;

    public TransaccionController(SistemaMonedero sistema) {
        this.sistema = sistema;
    }


    public static double convertirMonto(String texto) throws MontoInvalidoException {
        if (texto == null || texto.isBlank()) {
            throw new MontoInvalidoException("Debe ingresar un monto");
        }
        double monto;
        try {
            monto = Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new MontoInvalidoException("El monto '" + texto + "' no es un número válido");
        }
        if (monto <= 0) {
            throw new MontoInvalidoException("El monto debe ser mayor que cero");
        }
        return monto;
    }

    public List<Monedero> obtenerMonederos() {
        return sistema.obtenerTodosLosMonederos();
    }

    public Transaccion depositar(Monedero monedero, String montoTexto)
            throws MontoInvalidoException, RegistroNoEncontradoException, SaldoInsuficienteException {
        validarMonedero(monedero);
        return sistema.depositar(monedero.getCodigo(), convertirMonto(montoTexto));
    }

    public Transaccion retirar(Monedero monedero, String montoTexto)
            throws MontoInvalidoException, RegistroNoEncontradoException, SaldoInsuficienteException {
        validarMonedero(monedero);
        return sistema.retirar(monedero.getCodigo(), convertirMonto(montoTexto));
    }

    public Transaccion transferir(Monedero origen, Monedero destino, String montoTexto)
            throws MontoInvalidoException, RegistroNoEncontradoException, SaldoInsuficienteException {
        validarMonedero(origen);
        if (destino == null) {
            throw new IllegalArgumentException("Seleccione el monedero destino");
        }
        return sistema.transferir(origen.getCodigo(), destino.getCodigo(), convertirMonto(montoTexto));
    }

    public void revertir(Transaccion transaccion)
            throws RegistroNoEncontradoException, TransaccionNoReversibleException {
        if (transaccion == null) {
            throw new IllegalArgumentException("Seleccione una transacción del historial");
        }
        sistema.revertirTransaccion(transaccion.getCodigo());
    }

    public List<Transaccion> obtenerHistorial(Monedero monedero, TipoTransaccion filtro) {
        if (monedero == null) {
            return List.of();
        }
        return filtro == null ? monedero.getTransacciones() : monedero.filtrarPorTipo(filtro);
    }

    private void validarMonedero(Monedero monedero) {
        if (monedero == null) {
            throw new IllegalArgumentException("Seleccione un monedero");
        }
    }
}
