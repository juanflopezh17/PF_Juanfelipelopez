package co.edu.uniquindio.poo;

import co.edu.uniquindio.poo.model.AnalizadorGastos;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.DetectorAtipicas;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.SistemaMonedero;
import co.edu.uniquindio.poo.model.TipoAlerta;
import co.edu.uniquindio.poo.model.TipoMonedero;
import co.edu.uniquindio.poo.model.TipoTransaccion;
import co.edu.uniquindio.poo.model.Transaccion;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnalisisTest {

    private static final Logger LOG = Logger.getLogger(AnalisisTest.class.getName());

    @Test
    public void calculaPromedioYDesviacion() throws Exception {
        LOG.info("Inicio prueba calculaPromedioYDesviacion");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        Cliente cliente = new Cliente("Ana", "100", "", "");
        sistema.registrarCliente(cliente);
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        sistema.depositar(monedero.getCodigo(), 100);
        sistema.depositar(monedero.getCodigo(), 200);
        sistema.depositar(monedero.getCodigo(), 300);
        List<Transaccion> historial = cliente.obtenerTransacciones();

        assertEquals(200, DetectorAtipicas.calcularPromedio(historial), 0.001);
        assertEquals(81.6497, DetectorAtipicas.calcularDesviacion(historial), 0.001);
        assertEquals(363.299, DetectorAtipicas.calcularUmbral(historial), 0.001);
        LOG.info("Fin prueba calculaPromedioYDesviacion");
    }

    @Test
    public void transaccionGrandeSeMarcaSospechosaYAlerta() throws Exception {
        LOG.info("Inicio prueba transaccionGrandeSeMarcaSospechosaYAlerta");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        Cliente cliente = new Cliente("Ana", "100", "", "");
        sistema.registrarCliente(cliente);
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        sistema.depositar(monedero.getCodigo(), 1000);
        sistema.depositar(monedero.getCodigo(), 1100);
        sistema.depositar(monedero.getCodigo(), 900);
        Transaccion normal = sistema.depositar(monedero.getCodigo(), 1050);

        Transaccion grande = sistema.depositar(monedero.getCodigo(), 10000);

        assertFalse(normal.isSospechosa());
        assertTrue(grande.isSospechosa());
        assertTrue(cliente.getNotificaciones().stream()
                .anyMatch(n -> n.getTipo() == TipoAlerta.TRANSACCION_SOSPECHOSA));
        LOG.info("Fin prueba transaccionGrandeSeMarcaSospechosaYAlerta");
    }

    @Test
    public void historialCortoNoMarcaSospechosas() throws Exception {
        LOG.info("Inicio prueba historialCortoNoMarcaSospechosas");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        sistema.registrarCliente(new Cliente("Ana", "100", "", ""));
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        sistema.depositar(monedero.getCodigo(), 100);
        sistema.depositar(monedero.getCodigo(), 100);

        Transaccion grande = sistema.depositar(monedero.getCodigo(), 50000);

        assertFalse(grande.isSospechosa());
        LOG.info("Fin prueba historialCortoNoMarcaSospechosas");
    }

    @Test
    public void totalPorTipoAgrupaConStreams() throws Exception {
        LOG.info("Inicio prueba totalPorTipoAgrupaConStreams");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        Cliente cliente = new Cliente("Ana", "100", "", "");
        sistema.registrarCliente(cliente);
        Monedero ahorros = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        Monedero diario = sistema.crearMonedero("100", "Diario", TipoMonedero.GASTOS_DIARIOS);
        sistema.depositar(ahorros.getCodigo(), 2000);
        sistema.depositar(ahorros.getCodigo(), 1000);
        sistema.retirar(ahorros.getCodigo(), 300);
        sistema.transferir(ahorros.getCodigo(), diario.getCodigo(), 500);

        Map<TipoTransaccion, Double> totales = AnalizadorGastos.totalPorTipo(cliente);

        assertEquals(3000, totales.get(TipoTransaccion.DEPOSITO), 0.001);
        assertEquals(300, totales.get(TipoTransaccion.RETIRO), 0.001);
        assertEquals(500, totales.get(TipoTransaccion.TRANSFERENCIA), 0.001);
        assertEquals(400, AnalizadorGastos.promedioGasto(cliente), 0.001);
        LOG.info("Fin prueba totalPorTipoAgrupaConStreams");
    }

    @Test
    public void saldoBajoGeneraAlerta() throws Exception {
        LOG.info("Inicio prueba saldoBajoGeneraAlerta");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        Cliente cliente = new Cliente("Ana", "100", "", "");
        sistema.registrarCliente(cliente);
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        sistema.depositar(monedero.getCodigo(), 500);

        sistema.retirar(monedero.getCodigo(), 450);

        assertTrue(monedero.tieneSaldoBajo());
        assertTrue(cliente.getNotificaciones().stream()
                .anyMatch(n -> n.getTipo() == TipoAlerta.SALDO_BAJO));
        LOG.info("Fin prueba saldoBajoGeneraAlerta");
    }
}
