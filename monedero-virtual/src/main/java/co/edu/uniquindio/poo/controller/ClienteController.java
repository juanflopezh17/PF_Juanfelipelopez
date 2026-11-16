package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.RegistroNoEncontradoException;
import co.edu.uniquindio.poo.model.SistemaMonedero;
import co.edu.uniquindio.poo.model.TipoMonedero;

import java.util.List;

public class ClienteController {

    private final SistemaMonedero sistema;

    public ClienteController(SistemaMonedero sistema) {
        this.sistema = sistema;
    }

    public List<Cliente> obtenerClientes() {
        return sistema.getClientes();
    }

    public Cliente agregarCliente(String documento, String nombre, String correo, String telefono) {
        Cliente cliente = new Cliente(nombre, documento, correo, telefono);
        sistema.registrarCliente(cliente);
        return cliente;
    }

    public void actualizarCliente(String documento, String nombre, String correo, String telefono)
            throws RegistroNoEncontradoException {
        Cliente cliente = sistema.buscarCliente(documento);
        cliente.setNombre(nombre);
        cliente.setCorreo(correo);
        cliente.setTelefono(telefono);
    }

    public void eliminarCliente(String documento) throws RegistroNoEncontradoException {
        sistema.eliminarCliente(documento);
    }

    public Monedero crearMonedero(String documento, String nombre, TipoMonedero tipo)
            throws RegistroNoEncontradoException {
        return sistema.crearMonedero(documento, nombre, tipo);
    }
}
