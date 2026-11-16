package co.edu.uniquindio.poo;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.MontoInvalidoException;
import co.edu.uniquindio.poo.model.RegistroNoEncontradoException;
import co.edu.uniquindio.poo.model.SaldoInsuficienteException;
import co.edu.uniquindio.poo.model.SistemaMonedero;
import co.edu.uniquindio.poo.model.TipoMonedero;
import co.edu.uniquindio.poo.model.Transaccion;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class TransaccionTest {

    private static final Logger LOG = Logger.getLogger(TransaccionTest.class.getName());

    @Test
    public void depositoAumentaSaldoYGeneraPuntos() throws Exception {
        LOG.info("Inicio prueba depositoAumentaSaldoYGeneraPuntos");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        Cliente cliente = new Cliente("Ana", "100", "ana@correo.com", "300");
        sistema.registrarCliente(cliente);
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);

        Transaccion deposito = sistema.depositar(monedero.getCodigo(), 1500);

        assertEquals(1500, monedero.getSaldo(), 0.001);
        assertEquals(0, deposito.getComision(), 0.001);
        assertEquals(15, deposito.getPuntosGenerados());
        assertEquals(15, cliente.getPuntosAcumulados());
        LOG.info("Fin prueba depositoAumentaSaldoYGeneraPuntos");
    }

    @Test
    public void retiroSinSaldoLanzaExcepcion() throws Exception {
        LOG.info("Inicio prueba retiroSinSaldoLanzaExcepcion");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        sistema.registrarCliente(new Cliente("Ana", "100", "", ""));
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        sistema.depositar(monedero.getCodigo(), 500);

        assertThrows(SaldoInsuficienteException.class, () -> sistema.retirar(monedero.getCodigo(), 600));
        assertEquals(500, monedero.getSaldo(), 0.001);
        assertEquals(1, monedero.getTransacciones().size());
        LOG.info("Fin prueba retiroSinSaldoLanzaExcepcion");
    }

    @Test
    public void retiroCobraComisionDelUnoPorCiento() throws Exception {
        LOG.info("Inicio prueba retiroCobraComisionDelUnoPorCiento");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        sistema.registrarCliente(new Cliente("Ana", "100", "", ""));
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        sistema.depositar(monedero.getCodigo(), 1000);

        Transaccion retiro = sistema.retirar(monedero.getCodigo(), 200);

        assertEquals(2, retiro.getComision(), 0.001);
        assertEquals(798, monedero.getSaldo(), 0.001);
        assertEquals(4, retiro.getPuntosGenerados());
        LOG.info("Fin prueba retiroCobraComisionDelUnoPorCiento");
    }

    @Test
    public void transferenciaMueveSaldoYCobraComision() throws Exception {
        LOG.info("Inicio prueba transferenciaMueveSaldoYCobraComision");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        sistema.registrarCliente(new Cliente("Ana", "100", "", ""));
        sistema.registrarCliente(new Cliente("Luis", "200", "", ""));
        Monedero origen = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        Monedero destino = sistema.crearMonedero("200", "Principal", TipoMonedero.GASTOS_DIARIOS);
        sistema.depositar(origen.getCodigo(), 1000);

        Transaccion transferencia = sistema.transferir(origen.getCodigo(), destino.getCodigo(), 500);

        assertEquals(10, transferencia.getComision(), 0.001);
        assertEquals(490, origen.getSaldo(), 0.001);
        assertEquals(500, destino.getSaldo(), 0.001);
        assertEquals(15, transferencia.getPuntosGenerados());
        assertEquals(1, destino.getTransacciones().size());
        LOG.info("Fin prueba transferenciaMueveSaldoYCobraComision");
    }

    @Test
    public void montoInvalidoLanzaExcepcion() throws Exception {
        LOG.info("Inicio prueba montoInvalidoLanzaExcepcion");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        sistema.registrarCliente(new Cliente("Ana", "100", "", ""));
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);

        assertThrows(MontoInvalidoException.class, () -> sistema.depositar(monedero.getCodigo(), -50));
        assertThrows(MontoInvalidoException.class, () -> sistema.depositar(monedero.getCodigo(), 0));
        assertEquals(0, monedero.getSaldo(), 0.001);
        LOG.info("Fin prueba montoInvalidoLanzaExcepcion");
    }

    @Test
    public void monederoInexistenteLanzaExcepcion() {
        LOG.info("Inicio prueba monederoInexistenteLanzaExcepcion");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");

        assertThrows(RegistroNoEncontradoException.class, () -> sistema.depositar("NO-EXISTE", 100));
        assertThrows(RegistroNoEncontradoException.class, () -> sistema.buscarCliente("999"));
        LOG.info("Fin prueba monederoInexistenteLanzaExcepcion");
    }

    @Test
    public void clienteDuplicadoLanzaExcepcion() {
        LOG.info("Inicio prueba clienteDuplicadoLanzaExcepcion");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        sistema.registrarCliente(new Cliente("Ana", "100", "", ""));

        assertThrows(IllegalArgumentException.class,
                () -> sistema.registrarCliente(new Cliente("Otra Ana", "100", "", "")));
        assertEquals(1, sistema.getClientes().size());
        LOG.info("Fin prueba clienteDuplicadoLanzaExcepcion");
    }
}
