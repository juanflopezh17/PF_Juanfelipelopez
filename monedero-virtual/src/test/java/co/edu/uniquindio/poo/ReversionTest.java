package co.edu.uniquindio.poo;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.EstadoTransaccion;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.Rango;
import co.edu.uniquindio.poo.model.SistemaMonedero;
import co.edu.uniquindio.poo.model.TipoMonedero;
import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.model.TransaccionNoReversibleException;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReversionTest {

    private static final Logger LOG = Logger.getLogger(ReversionTest.class.getName());

    @Test
    public void revertirDepositoQuitaSaldoYPuntos() throws Exception {
        LOG.info("Inicio prueba revertirDepositoQuitaSaldoYPuntos");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        Cliente cliente = new Cliente("Ana", "100", "", "");
        sistema.registrarCliente(cliente);
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        sistema.depositar(monedero.getCodigo(), 1000);
        Transaccion deposito = sistema.depositar(monedero.getCodigo(), 2000);

        sistema.revertirTransaccion(deposito.getCodigo());

        assertEquals(EstadoTransaccion.REVERTIDA, deposito.getEstado());
        assertEquals(1000, monedero.getSaldo(), 0.001);
        assertEquals(10, cliente.getPuntosAcumulados());
        LOG.info("Fin prueba revertirDepositoQuitaSaldoYPuntos");
    }

    @Test
    public void revertirRetiroDevuelveMontoYComision() throws Exception {
        LOG.info("Inicio prueba revertirRetiroDevuelveMontoYComision");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        sistema.registrarCliente(new Cliente("Ana", "100", "", ""));
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        sistema.depositar(monedero.getCodigo(), 1000);
        Transaccion retiro = sistema.retirar(monedero.getCodigo(), 500);

        sistema.revertirTransaccion(retiro.getCodigo());

        assertEquals(1000, monedero.getSaldo(), 0.001);
        LOG.info("Fin prueba revertirRetiroDevuelveMontoYComision");
    }

    @Test
    public void revertirDosVecesLanzaExcepcion() throws Exception {
        LOG.info("Inicio prueba revertirDosVecesLanzaExcepcion");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        sistema.registrarCliente(new Cliente("Ana", "100", "", ""));
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        Transaccion deposito = sistema.depositar(monedero.getCodigo(), 1000);
        sistema.revertirTransaccion(deposito.getCodigo());

        assertThrows(TransaccionNoReversibleException.class,
                () -> sistema.revertirTransaccion(deposito.getCodigo()));
        assertEquals(0, monedero.getSaldo(), 0.001);
        LOG.info("Fin prueba revertirDosVecesLanzaExcepcion");
    }

    @Test
    public void revertirDepositoGastadoLanzaExcepcion() throws Exception {
        LOG.info("Inicio prueba revertirDepositoGastadoLanzaExcepcion");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        sistema.registrarCliente(new Cliente("Ana", "100", "", ""));
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        Transaccion deposito = sistema.depositar(monedero.getCodigo(), 1000);
        sistema.retirar(monedero.getCodigo(), 900);

        assertThrows(TransaccionNoReversibleException.class,
                () -> sistema.revertirTransaccion(deposito.getCodigo()));
        assertEquals(EstadoTransaccion.EXITOSA, deposito.getEstado());
        LOG.info("Fin prueba revertirDepositoGastadoLanzaExcepcion");
    }

    @Test
    public void revertirRecalculaElRango() throws Exception {
        LOG.info("Inicio prueba revertirRecalculaElRango");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        Cliente cliente = new Cliente("Ana", "100", "", "");
        sistema.registrarCliente(cliente);
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        Transaccion grande = sistema.depositar(monedero.getCodigo(), 60000);
        assertEquals(Rango.PLATA, cliente.getRango());

        sistema.revertirTransaccion(grande.getCodigo());

        assertEquals(Rango.BRONCE, cliente.getRango());
        assertEquals(0, cliente.getPuntosAcumulados());
        LOG.info("Fin prueba revertirRecalculaElRango");
    }
}
