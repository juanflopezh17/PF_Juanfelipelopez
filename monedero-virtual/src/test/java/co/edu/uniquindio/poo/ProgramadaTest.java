package co.edu.uniquindio.poo;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Frecuencia;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.SistemaMonedero;
import co.edu.uniquindio.poo.model.TipoAlerta;
import co.edu.uniquindio.poo.model.TipoMonedero;
import co.edu.uniquindio.poo.model.TipoTransaccion;
import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.model.TransaccionProgramada;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProgramadaTest {

    private static final Logger LOG = Logger.getLogger(ProgramadaTest.class.getName());

    @Test
    public void programadasQuedanOrdenadasPorFecha() throws Exception {
        LOG.info("Inicio prueba programadasQuedanOrdenadasPorFecha");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        sistema.registrarCliente(new Cliente("Ana", "100", "", ""));
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        LocalDate hoy = LocalDate.now();

        sistema.programarTransaccion(TipoTransaccion.DEPOSITO, 100, monedero.getCodigo(), null, hoy.plusDays(10), Frecuencia.MENSUAL);
        sistema.programarTransaccion(TipoTransaccion.DEPOSITO, 200, monedero.getCodigo(), null, hoy.plusDays(2), Frecuencia.MENSUAL);
        sistema.programarTransaccion(TipoTransaccion.DEPOSITO, 300, monedero.getCodigo(), null, hoy.plusDays(5), Frecuencia.MENSUAL);

        List<LocalDate> fechas = sistema.getProgramadas().stream()
                .map(TransaccionProgramada::getFechaEjecucion)
                .collect(Collectors.toList());
        assertIterableEquals(List.of(hoy.plusDays(2), hoy.plusDays(5), hoy.plusDays(10)), fechas);
        LOG.info("Fin prueba programadasQuedanOrdenadasPorFecha");
    }

    @Test
    public void procesarEjecutaPendientesYReprograma() throws Exception {
        LOG.info("Inicio prueba procesarEjecutaPendientesYReprograma");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        sistema.registrarCliente(new Cliente("Ana", "100", "", ""));
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        LocalDate hoy = LocalDate.now();
        TransaccionProgramada programada = sistema.programarTransaccion(TipoTransaccion.DEPOSITO, 300,
                monedero.getCodigo(), null, hoy.minusDays(14), Frecuencia.SEMANAL);

        List<Transaccion> ejecutadas = sistema.procesarProgramadas(hoy);

        assertEquals(3, ejecutadas.size()); // hace 14 días, hace 7 días y hoy
        assertEquals(900, monedero.getSaldo(), 0.001);
        assertEquals(hoy.plusDays(7), programada.getFechaEjecucion());
        LOG.info("Fin prueba procesarEjecutaPendientesYReprograma");
    }

    @Test
    public void programadaSinSaldoNoSeEjecutaYAvisa() throws Exception {
        LOG.info("Inicio prueba programadaSinSaldoNoSeEjecutaYAvisa");
        SistemaMonedero sistema = new SistemaMonedero("Prueba");
        Cliente cliente = new Cliente("Ana", "100", "", "");
        sistema.registrarCliente(cliente);
        Monedero monedero = sistema.crearMonedero("100", "Ahorros", TipoMonedero.AHORROS);
        LocalDate hoy = LocalDate.now();
        TransaccionProgramada programada = sistema.programarTransaccion(TipoTransaccion.RETIRO, 500,
                monedero.getCodigo(), null, hoy, Frecuencia.MENSUAL);

        List<Transaccion> ejecutadas = sistema.procesarProgramadas(hoy);

        assertEquals(0, ejecutadas.size());
        assertEquals(hoy.plusMonths(1), programada.getFechaEjecucion());
        assertTrue(cliente.getNotificaciones().stream()
                .anyMatch(n -> n.getTipo() == TipoAlerta.RECORDATORIO));
        LOG.info("Fin prueba programadaSinSaldoNoSeEjecutaYAvisa");
    }
}
