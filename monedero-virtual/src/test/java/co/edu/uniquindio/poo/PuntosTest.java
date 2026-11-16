package co.edu.uniquindio.poo;

import co.edu.uniquindio.poo.model.BonoSaldo;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.DescuentoTransferencia;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.PuntosInsuficientesException;
import co.edu.uniquindio.poo.model.Rango;
import co.edu.uniquindio.poo.model.RetirosSinCargo;
import co.edu.uniquindio.poo.model.SistemaMonedero;
import co.edu.uniquindio.poo.model.TipoMonedero;
import co.edu.uniquindio.poo.model.Transaccion;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PuntosTest {

    private static final Logger LOG = Logger.getLogger(PuntosTest.class.getName());

    @Test
    public void rangoSeCalculaSegunPuntos() {
        LOG.info("Inicio prueba rangoSeCalculaSegunPuntos");
        assertEquals(Rango.BRONCE, Rango.calcularRango(0));
        assertEquals(Rango.BRONCE, Rango.calcularRango(500));
        assertEquals(Rango.PLATA, Rango.calcularRango(501));
        assertEquals(Rango.PLATA, Rango.calcularRango(1000));
        assertEquals(Rango.ORO, Rango.calcularRango(1001));
        assertEquals(Rango.ORO, Rango.calcularRango(5000));
        assertEquals(Rango.PLATINO, Rango.calcularRango(5001));
        LOG.info("Fin prueba rangoSeCalculaSegunPuntos");
    }

    @Test
    public void clienteSubeDeRangoYRecibeBonoDePuntos() throws Exception {
        LOG.info("Inicio prueba clienteSubeDeRangoYRecibeBonoDePuntos");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        Cliente cliente = new Cliente("Ana", "100", "", "");
        sistema.registrarCliente(cliente);
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);

        sistema.depositar(monedero.getCodigo(), 60000);           // 600 puntos -> PLATA
        Transaccion segundo = sistema.depositar(monedero.getCodigo(), 1000); // 10 + 5% de bono = 11

        assertEquals(Rango.PLATA, cliente.getRango());
        assertEquals(11, segundo.getPuntosGenerados());
        assertEquals(611, cliente.getPuntosAcumulados());
        LOG.info("Fin prueba clienteSubeDeRangoYRecibeBonoDePuntos");
    }

    @Test
    public void canjeSinPuntosLanzaExcepcion() {
        LOG.info("Inicio prueba canjeSinPuntosLanzaExcepcion");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        Cliente cliente = new Cliente("Ana", "100", "", "");
        sistema.registrarCliente(cliente);

        assertThrows(PuntosInsuficientesException.class,
                () -> sistema.canjearBeneficio("100", new RetirosSinCargo()));
        assertEquals(0, cliente.getBeneficios().size());
        LOG.info("Fin prueba canjeSinPuntosLanzaExcepcion");
    }

    @Test
    public void descuentoReduceComisionDeTransferencia() throws Exception {
        LOG.info("Inicio prueba descuentoReduceComisionDeTransferencia");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        Cliente cliente = new Cliente("Ana", "100", "", "");
        sistema.registrarCliente(cliente);
        Monedero ahorros = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        Monedero diario = sistema.crearMonedero("100", "Diario", TipoMonedero.GASTOS_DIARIOS);
        sistema.depositar(ahorros.getCodigo(), 15000); // 150 puntos

        sistema.canjearBeneficio("100", new DescuentoTransferencia());
        Transaccion transferencia = sistema.transferir(ahorros.getCodigo(), diario.getCodigo(), 1000);

        assertEquals(50, cliente.getPuntosDisponibles() - transferencia.getPuntosGenerados());
        assertEquals(150 + transferencia.getPuntosGenerados(), cliente.getPuntosAcumulados());
        assertEquals(18, transferencia.getComision(), 0.001); // 20 - 10%
        LOG.info("Fin prueba descuentoReduceComisionDeTransferencia");
    }

    @Test
    public void retirosSinCargoNoCobraComision() throws Exception {
        LOG.info("Inicio prueba retirosSinCargoNoCobraComision");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        Cliente cliente = new Cliente("Ana", "100", "", "");
        sistema.registrarCliente(cliente);
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        sistema.depositar(monedero.getCodigo(), 50000); // 500 puntos

        sistema.canjearBeneficio("100", new RetirosSinCargo());
        Transaccion retiro = sistema.retirar(monedero.getCodigo(), 1000);

        assertEquals(0, retiro.getComision(), 0.001);
        assertEquals(49000, monedero.getSaldo(), 0.001);
        LOG.info("Fin prueba retirosSinCargoNoCobraComision");
    }

    @Test
    public void bonoSaldoAbonaYNoBajaElRango() throws Exception {
        LOG.info("Inicio prueba bonoSaldoAbonaYNoBajaElRango");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        Cliente cliente = new Cliente("Ana", "100", "", "");
        sistema.registrarCliente(cliente);
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        sistema.depositar(monedero.getCodigo(), 100000); // 1000 puntos -> PLATA

        sistema.canjearBeneficio("100", new BonoSaldo(monedero));

        assertEquals(100050, monedero.getSaldo(), 0.001);
        assertEquals(0, cliente.getPuntosDisponibles());
        assertEquals(1000, cliente.getPuntosAcumulados());
        assertEquals(Rango.PLATA, cliente.getRango());
        LOG.info("Fin prueba bonoSaldoAbonaYNoBajaElRango");
    }
}
