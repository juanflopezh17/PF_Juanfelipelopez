package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.model.Frecuencia;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.MontoInvalidoException;
import co.edu.uniquindio.poo.model.RegistroNoEncontradoException;
import co.edu.uniquindio.poo.model.SistemaMonedero;
import co.edu.uniquindio.poo.model.TipoTransaccion;
import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.model.TransaccionProgramada;

import java.time.LocalDate;
import java.util.List;

public class ProgramadaController {

    private final SistemaMonedero sistema;

    public ProgramadaController(SistemaMonedero sistema) {
        this.sistema = sistema;
    }

    public List<TransaccionProgramada> obtenerProgramadas() {
        return sistema.getProgramadas();
    }

    public List<Monedero> obtenerMonederos() {
        return sistema.obtenerTodosLosMonederos();
    }

    public TransaccionProgramada programar(TipoTransaccion tipo, String montoTexto, Monedero origen,
                                           Monedero destino, LocalDate fecha, Frecuencia frecuencia)
            throws MontoInvalidoException, RegistroNoEncontradoException {
        if (tipo == null || origen == null || fecha == null || frecuencia == null) {
            throw new IllegalArgumentException("Complete tipo, monedero, fecha y frecuencia");
        }
        if (tipo == TipoTransaccion.TRANSFERENCIA && destino == null) {
            throw new IllegalArgumentException("Seleccione el monedero destino de la transferencia");
        }
        double monto = TransaccionController.convertirMonto(montoTexto);
        String codigoDestino = destino == null ? null : destino.getCodigo();
        return sistema.programarTransaccion(tipo, monto, origen.getCodigo(), codigoDestino, fecha, frecuencia);
    }

    public void cancelar(TransaccionProgramada programada) throws RegistroNoEncontradoException {
        if (programada == null) {
            throw new IllegalArgumentException("Seleccione una transacción programada");
        }
        sistema.cancelarProgramada(programada.getCodigo());
    }

    public List<Transaccion> procesarPendientes(LocalDate hasta) {
        return sistema.procesarProgramadas(hasta == null ? LocalDate.now() : hasta);
    }

    public void enviarRecordatorios(LocalDate hoy) {
        sistema.enviarRecordatorios(hoy == null ? LocalDate.now() : hoy);
    }
}
