package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.App;
import co.edu.uniquindio.poo.controller.ProgramadaController;
import co.edu.uniquindio.poo.model.Frecuencia;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.MontoInvalidoException;
import co.edu.uniquindio.poo.model.RegistroNoEncontradoException;
import co.edu.uniquindio.poo.model.TipoTransaccion;
import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.model.TransaccionProgramada;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;

public class ProgramadaViewController {

    private App app;
    private ProgramadaController controller;

    @FXML private ComboBox<TipoTransaccion> cmbTipo;
    @FXML private ComboBox<Monedero> cmbOrigen;
    @FXML private ComboBox<Monedero> cmbDestino;
    @FXML private TextField txtMonto;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<Frecuencia> cmbFrecuencia;
    @FXML private DatePicker dpProcesar;

    @FXML private TableView<TransaccionProgramada> tblProgramadas;
    @FXML private TableColumn<TransaccionProgramada, String> colCodigo;
    @FXML private TableColumn<TransaccionProgramada, String> colTipo;
    @FXML private TableColumn<TransaccionProgramada, String> colMonto;
    @FXML private TableColumn<TransaccionProgramada, String> colOrigen;
    @FXML private TableColumn<TransaccionProgramada, String> colDestino;
    @FXML private TableColumn<TransaccionProgramada, String> colFecha;
    @FXML private TableColumn<TransaccionProgramada, String> colFrecuencia;
    @FXML private TableColumn<TransaccionProgramada, String> colActiva;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void initialize() {
        controller = new ProgramadaController(App.sistema);

        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().toString()));
        colMonto.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getMonto())));
        colOrigen.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMonederoOrigen().toString()));
        colDestino.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getMonederoDestino() == null ? "-" : c.getValue().getMonederoDestino().toString()));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaEjecucion().toString()));
        colFrecuencia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFrecuencia().toString()));
        colActiva.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActiva() ? "Activa" : "Cancelada"));

        cmbTipo.setItems(FXCollections.observableArrayList(TipoTransaccion.values()));
        cmbFrecuencia.setItems(FXCollections.observableArrayList(Frecuencia.values()));
        cmbOrigen.setItems(FXCollections.observableArrayList(controller.obtenerMonederos()));
        cmbDestino.setItems(FXCollections.observableArrayList(controller.obtenerMonederos()));
        cmbDestino.setDisable(true);
        dpFecha.setValue(LocalDate.now());
        dpProcesar.setValue(LocalDate.now());
        refrescarTabla();
    }

    @FXML
    void cambiarTipo() {
        boolean esTransferencia = cmbTipo.getValue() == TipoTransaccion.TRANSFERENCIA;
        cmbDestino.setDisable(!esTransferencia);
        if (!esTransferencia) {
            cmbDestino.getSelectionModel().clearSelection();
        }
    }

    @FXML
    void programar() {
        try {
            TransaccionProgramada p = controller.programar(cmbTipo.getValue(), txtMonto.getText(),
                    cmbOrigen.getValue(), cmbDestino.getValue(), dpFecha.getValue(), cmbFrecuencia.getValue());
            Mensajes.info("Transacción programada", "Se programó " + p.getCodigo()
                    + ". La lista se reordenó por fecha de ejecución.");
            txtMonto.clear();
            refrescarTabla();
        } catch (MontoInvalidoException | RegistroNoEncontradoException | IllegalArgumentException e) {
            Mensajes.error("No se pudo programar", e.getMessage());
        }
    }

    @FXML
    void cancelar() {
        try {
            controller.cancelar(tblProgramadas.getSelectionModel().getSelectedItem());
            refrescarTabla();
        } catch (RegistroNoEncontradoException | IllegalArgumentException e) {
            Mensajes.error("No se pudo cancelar", e.getMessage());
        }
    }

    @FXML
    void procesarPendientes() {
        List<Transaccion> ejecutadas = controller.procesarPendientes(dpProcesar.getValue());
        StringBuilder sb = new StringBuilder("Se ejecutaron " + ejecutadas.size() + " transacciones");
        ejecutadas.forEach(t -> sb.append("\n").append(t));
        sb.append("\n\nSi alguna falló por saldo, el cliente recibió una alerta.");
        Mensajes.info("Procesamiento", sb.toString());
        refrescarTabla();
    }

    @FXML
    void enviarRecordatorios() {
        controller.enviarRecordatorios(LocalDate.now());
        Mensajes.info("Recordatorios", "Se enviaron los recordatorios de las transacciones de mañana.\n"
                + "Puedes verlos en la consola y en Reportes y alertas.");
    }

    @FXML
    void volver() {
        app.openViewPrincipal();
    }

    private void refrescarTabla() {
        tblProgramadas.setItems(FXCollections.observableArrayList(controller.obtenerProgramadas()));
        tblProgramadas.refresh();
    }
}
