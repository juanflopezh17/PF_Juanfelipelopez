package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.model.Beneficio;
import co.edu.uniquindio.poo.model.BonoSaldo;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.DescuentoTransferencia;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.PuntosInsuficientesException;
import co.edu.uniquindio.poo.model.RegistroNoEncontradoException;
import co.edu.uniquindio.poo.model.RetirosSinCargo;
import co.edu.uniquindio.poo.model.SistemaMonedero;

import java.util.List;

public class PuntosController {

    private final SistemaMonedero sistema;

    public PuntosController(SistemaMonedero sistema) {
        this.sistema = sistema;
    }

    public List<Cliente> obtenerClientes() {
        return sistema.getClientes();
    }

    public void canjearDescuentoTransferencia(Cliente cliente)
            throws RegistroNoEncontradoException, PuntosInsuficientesException {
        canjear(cliente, new DescuentoTransferencia());
    }

    public void canjearRetirosSinCargo(Cliente cliente)
            throws RegistroNoEncontradoException, PuntosInsuficientesException {
        canjear(cliente, new RetirosSinCargo());
    }

    public void canjearBonoSaldo(Cliente cliente, Monedero monedero)
            throws RegistroNoEncontradoException, PuntosInsuficientesException {
        if (monedero == null) {
            throw new IllegalArgumentException("Seleccione el monedero que recibe el bono");
        }
        canjear(cliente, new BonoSaldo(monedero));
    }

    private void canjear(Cliente cliente, Beneficio beneficio)
            throws RegistroNoEncontradoException, PuntosInsuficientesException {
        if (cliente == null) {
            throw new IllegalArgumentException("Seleccione un cliente");
        }
        sistema.canjearBeneficio(cliente.getDocumento(), beneficio);
    }
}
