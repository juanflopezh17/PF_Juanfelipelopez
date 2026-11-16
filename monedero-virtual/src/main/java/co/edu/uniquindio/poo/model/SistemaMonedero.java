package co.edu.uniquindio.poo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SistemaMonedero {

    private String nombre;
    private List<Cliente> clientes;
    private List<TransaccionProgramada> programadas;
    private List<INotificador> notificadores;
    private int consecutivo;
    private int consecutivoProgramadas;

    public SistemaMonedero(String nombre) {
        this.nombre = nombre;
        this.clientes = new ArrayList<>();
        this.programadas = new ArrayList<>();
        this.notificadores = new ArrayList<>();
        this.notificadores.add(new NotificadorEmail());
        this.notificadores.add(new NotificadorSMS());
        this.notificadores.add(new NotificadorWhatsApp());
        this.consecutivo = 0;
        this.consecutivoProgramadas = 0;
    }

    public void registrarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        if (existeCliente(cliente.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un cliente con el documento " + cliente.getDocumento());
        }
        clientes.add(cliente);
    }

    public boolean existeCliente(String documento) {
        return clientes.stream().anyMatch(c -> c.getDocumento().equals(documento));
    }

    public Cliente buscarCliente(String documento) throws RegistroNoEncontradoException {
        return clientes.stream()
                .filter(c -> c.getDocumento().equals(documento))
                .findFirst()
                .orElseThrow(() -> new RegistroNoEncontradoException("No existe un cliente con documento " + documento));
    }

    public void eliminarCliente(String documento) throws RegistroNoEncontradoException {
        Cliente cliente = buscarCliente(documento);
        if (cliente.getSaldoTotal() > 0) {
            throw new IllegalStateException("No se puede eliminar un cliente con saldo en sus monederos");
        }
        clientes.remove(cliente);
    }

    public Monedero crearMonedero(String documento, String nombreMonedero, TipoMonedero tipo)
            throws RegistroNoEncontradoException {
        Cliente cliente = buscarCliente(documento);
        String codigo = documento + "-" + (cliente.getMonederos().size() + 1);
        Monedero monedero = new Monedero(codigo, nombreMonedero, tipo, cliente);
        cliente.agregarMonedero(monedero);
        return monedero;
    }

    public Monedero buscarMonedero(String codigo) throws RegistroNoEncontradoException {
        return clientes.stream()
                .flatMap(c -> c.getMonederos().stream())
                .filter(m -> m.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new RegistroNoEncontradoException("No existe el monedero " + codigo));
    }

    public List<Monedero> obtenerTodosLosMonederos() {
        return clientes.stream()
                .flatMap(c -> c.getMonederos().stream())
                .collect(Collectors.toList());
    }

    public Transaccion depositar(String codigoMonedero, double monto)
            throws RegistroNoEncontradoException, MontoInvalidoException, SaldoInsuficienteException {
        Monedero monedero = buscarMonedero(codigoMonedero);
        Transaccion transaccion = new Deposito(generarCodigo(), monto, monedero);
        procesarTransaccion(transaccion, monedero.getPropietario());
        return transaccion;
    }

    public Transaccion retirar(String codigoMonedero, double monto)
            throws RegistroNoEncontradoException, MontoInvalidoException, SaldoInsuficienteException {
        Monedero monedero = buscarMonedero(codigoMonedero);
        Transaccion transaccion = new Retiro(generarCodigo(), monto, monedero);
        procesarTransaccion(transaccion, monedero.getPropietario());
        return transaccion;
    }

    public Transaccion transferir(String codigoOrigen, String codigoDestino, double monto)
            throws RegistroNoEncontradoException, MontoInvalidoException, SaldoInsuficienteException {
        Monedero origen = buscarMonedero(codigoOrigen);
        Monedero destino = buscarMonedero(codigoDestino);
        Transaccion transaccion = new Transferencia(generarCodigo(), monto, origen, destino);
        procesarTransaccion(transaccion, origen.getPropietario());
        return transaccion;
    }

    private void procesarTransaccion(Transaccion transaccion, Cliente cliente) throws SaldoInsuficienteException {
        List<Transaccion> historialPrevio = cliente.obtenerTransacciones().stream()
                .filter(t -> !t.estaRevertida())
                .collect(Collectors.toList());

        transaccion.setComision(cliente.calcularComisionFinal(transaccion));
        transaccion.ejecutar(); cliente.registrarUsoBeneficios(transaccion);

        int puntos = cliente.getRango().aplicarBono(transaccion.calcularPuntos());
        transaccion.setPuntosGenerados(puntos);
        cliente.sumarPuntos(puntos, transaccion.getCodigo());

        if (DetectorAtipicas.esAtipica(transaccion, historialPrevio)) {
            transaccion.marcarSospechosa();
            notificar(cliente, String.format(
                    "Detectamos un movimiento inusual (%s por %.2f). Si no fuiste tú, revisa tu cuenta.",
                    transaccion.getTipo(), transaccion.getMonto()), TipoAlerta.TRANSACCION_SOSPECHOSA);
        }
        if (cliente.actualizarRango()) {
            notificar(cliente, "¡Tu rango cambió! Ahora eres " + cliente.getRango(), TipoAlerta.CAMBIO_RANGO);
        }
        if (transaccion.getMonederoOrigen().tieneSaldoBajo()) {
            notificar(cliente, String.format("El monedero '%s' tiene saldo bajo: %.2f",
                    transaccion.getMonederoOrigen().getNombre(), transaccion.getMonederoOrigen().getSaldo()),
                    TipoAlerta.SALDO_BAJO);
        }
    }

    public Transaccion buscarTransaccion(String codigo) throws RegistroNoEncontradoException {
        return clientes.stream()
                .flatMap(c -> c.obtenerTransacciones().stream())
                .filter(t -> t.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new RegistroNoEncontradoException("No existe la transacción " + codigo));
    }

    public void revertirTransaccion(String codigo)
            throws RegistroNoEncontradoException, TransaccionNoReversibleException {
        Transaccion transaccion = buscarTransaccion(codigo);
        transaccion.revertir();
        Cliente cliente = transaccion.getMonederoOrigen().getPropietario();
        cliente.restarPuntos(transaccion.getPuntosGenerados(), transaccion.getCodigo());
        if (cliente.actualizarRango()) {
            notificar(cliente, "Por la reversión de " + codigo + " tu rango ahora es " + cliente.getRango(),
                    TipoAlerta.CAMBIO_RANGO);
        }
    }

    public double consultarSaldo(String codigoMonedero) throws RegistroNoEncontradoException {
        return buscarMonedero(codigoMonedero).getSaldo();
    }

    public List<Transaccion> consultarHistorial(String codigoMonedero) throws RegistroNoEncontradoException {
        return buscarMonedero(codigoMonedero).getTransacciones();
    }


    public void canjearBeneficio(String documento, Beneficio beneficio)
            throws RegistroNoEncontradoException, PuntosInsuficientesException {
        Cliente cliente = buscarCliente(documento);
        cliente.canjearBeneficio(beneficio);
    }

    public void programarTransaccion(TransaccionProgramada programada) {
        if (programada == null) {
            throw new IllegalArgumentException("La transacción programada no puede ser nula");
        }
        programadas.add(programada);
        ordenarProgramadas();
    }

    public TransaccionProgramada programarTransaccion(TipoTransaccion tipo, double monto, String codigoOrigen,
                                                      String codigoDestino, LocalDate fecha, Frecuencia frecuencia)
            throws RegistroNoEncontradoException, MontoInvalidoException {
        Monedero origen = buscarMonedero(codigoOrigen);
        Monedero destino = tipo == TipoTransaccion.TRANSFERENCIA ? buscarMonedero(codigoDestino) : null;
        consecutivoProgramadas++;
        String codigo = String.format("P-%03d", consecutivoProgramadas);
        TransaccionProgramada programada =
                new TransaccionProgramada(codigo, tipo, monto, origen, destino, fecha, frecuencia);
        programarTransaccion(programada);
        return programada;
    }

    public void ordenarProgramadas() {
        for (int i = 1; i < programadas.size(); i++) {
            TransaccionProgramada actual = programadas.get(i);
            int j = i - 1;
            while (j >= 0 && programadas.get(j).compareTo(actual) > 0) {
                programadas.set(j + 1, programadas.get(j));
                j--;
            }
            programadas.set(j + 1, actual);
        }
    }

    public List<Transaccion> procesarProgramadas(LocalDate hoy) {
        ordenarProgramadas();
        List<Transaccion> ejecutadas = new ArrayList<>();
        for (TransaccionProgramada programada : programadas) {
            Cliente cliente = programada.getMonederoOrigen().getPropietario();
            while (programada.estaPendiente(hoy)) {
                try {
                    Transaccion transaccion = programada.generarTransaccion(generarCodigo());
                    transaccion.setFecha(programada.getFechaEjecucion().atStartOfDay());
                    procesarTransaccion(transaccion, cliente);
                    ejecutadas.add(transaccion);
                } catch (SaldoInsuficienteException | MontoInvalidoException e) {
                    notificar(cliente, "No se pudo ejecutar la transacción programada "
                            + programada.getCodigo() + ": " + e.getMessage(), TipoAlerta.RECORDATORIO);
                }
                programada.reprogramar();
            }
        }
        ordenarProgramadas();
        return ejecutadas;
    }

    public void enviarRecordatorios(LocalDate hoy) {
        programadas.stream()
                .filter(p -> p.isActiva() && p.getFechaEjecucion().equals(hoy.plusDays(1)))
                .forEach(p -> notificar(p.getMonederoOrigen().getPropietario(),
                        String.format("Recordatorio: mañana se ejecuta %s por %.2f desde '%s'",
                                p.getTipo(), p.getMonto(), p.getMonederoOrigen().getNombre()),
                        TipoAlerta.RECORDATORIO));
    }

    public void cancelarProgramada(String codigo) throws RegistroNoEncontradoException {
        TransaccionProgramada programada = programadas.stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new RegistroNoEncontradoException("No existe la programada " + codigo));
        programada.setActiva(false);
    }

    public String generarReporte(String documento) throws RegistroNoEncontradoException {
        return AnalizadorGastos.generarReporte(buscarCliente(documento));
    }

    private void notificar(Cliente cliente, String mensaje, TipoAlerta tipo) {
        String canales = notificadores.stream()
                .map(INotificador::getCanal)
                .collect(Collectors.joining(", "));
        Notificacion notificacion = new Notificacion(mensaje, tipo, canales);
        notificadores.forEach(n -> n.enviar(cliente, notificacion));
        cliente.recibirNotificacion(notificacion);
    }

    private String generarCodigo() {
        consecutivo++;
        return String.format("TX-%04d", consecutivo);
    }

    public String getNombre() {
        return nombre;
    }

    public List<Cliente> getClientes() {
        return Collections.unmodifiableList(clientes);
    }

    public List<TransaccionProgramada> getProgramadas() {
        return Collections.unmodifiableList(programadas);
    }

    public List<INotificador> getNotificadores() {
        return Collections.unmodifiableList(notificadores);
    }
}
