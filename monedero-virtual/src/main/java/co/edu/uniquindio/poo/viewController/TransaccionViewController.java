package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.App;
import co.edu.uniquindio.poo.controller.TransaccionController;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.MontoInvalidoException;
import co.edu.uniquindio.poo.model.RegistroNoEncontradoException;
import co.edu.uniquindio.poo.model.SaldoInsuficienteException;
import co.edu.uniquindio.poo.model.TipoTransaccion;
import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.model.TransaccionNoReversibleException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TransaccionViewController {

    private App app;
    private TransaccionController controller;

    @FXML private ComboBox<Monedero> cmbMonedero;
    @FXML private ComboBox<Monedero> cmbDestino;
    @FXML private TextField txtMonto;
    @FXML private Label lblSaldo;
    @FXML private Label lblCliente;
    @FXML private ComboBox<TipoTransaccion> cmbFiltro;

    @FXML private TableView<Transaccion> tblHistorial;
    @FXML private TableColumn<Transaccion, String> colCodigo;
    @FXML private TableColumn<Transaccion, String> colFecha;
    @FXML private TableColumn<Transaccion, String> colTipo;
    @FXML private TableColumn<Transaccion, String> colMonto;
    @FXML private TableColumn<Transaccion, String> colComision;
    @FXML private TableColumn<Transaccion, String> colPuntos;
    @FXML private TableColumn<Transaccion, String> colEstado;
    @FXML private TableColumn<Transaccion, String> colSospechosa;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void initialize() {
        controller = new TransaccionController(App.sistema);

        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaTexto()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().toString()));
        colMonto.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getMonto())));
        colComision.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getComision())));
        colPuntos.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPuntosGenerados())));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().toString()));
        colSospechosa.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isSospechosa() ? "SÍ" : ""));

        cmbMonedero.setItems(FXCollections.observableArrayList(controller.obtenerMonederos()));
        cmbDestino.setItems(FXCollections.observableArrayList(controller.obtenerMonederos()));
        cmbFiltro.setItems(FXCollections.observableArrayList(TipoTransaccion.values()));
        actualizarVista();
    }

    @FXML
    void depositar() {
        try {
            Transaccion t = controller.depositar(cmbMonedero.getValue(), txtMonto.getText());
            informar(t);
        } catch (MontoInvalidoException | SaldoInsuficienteException | RegistroNoEncontradoException
                 | IllegalArgumentException e) {
            Mensajes.error("No se pudo depositar", e.getMessage());
        }
    }

    @FXML
    void retirar() {
        try {
            Transaccion t = controller.retirar(cmbMonedero.getValue(), txtMonto.getText());
            informar(t);
        } catch (SaldoInsuficienteException e) {
            Mensajes.error("Saldo insuficiente", e.getMessage());
        } catch (MontoInvalidoException | RegistroNoEncontradoException | IllegalArgumentException e) {
            Mensajes.error("No se pudo retirar", e.getMessage());
        }
    }

    @FXML
    void transferir() {
        try {
            Transaccion t = controller.transferir(cmbMonedero.getValue(), cmbDestino.getValue(), txtMonto.getText());
            informar(t);
        } catch (SaldoInsuficienteException e) {
            Mensajes.error("Saldo insuficiente", e.getMessage());
        } catch (MontoInvalidoException | RegistroNoEncontradoException | IllegalArgumentException e) {
            Mensajes.error("No se pudo transferir", e.getMessage());
        }
    }

    @FXML
    void revertir() {
        Transaccion seleccionada = tblHistorial.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            Mensajes.advertencia("Revertir", "Seleccione una transacción del historial");
            return;
        }
        if (!Mensajes.confirmar("Revertir", "¿Revertir la transacción " + seleccionada.getCodigo()
                + "? Se descontarán sus " + seleccionada.getPuntosGenerados() + " puntos.")) {
            return;
        }
        try {
            controller.revertir(seleccionada);
            Mensajes.info("Transacción revertida", "Se revirtió " + seleccionada.getCodigo()
                    + " y se recalcularon los puntos del cliente");
            actualizarVista();
        } catch (RegistroNoEncontradoException | TransaccionNoReversibleException | IllegalArgumentException e) {
            Mensajes.error("No se pudo revertir", e.getMessage());
        }
    }

    @FXML
    void quitarFiltro() {
        cmbFiltro.getSelectionModel().clearSelection();
        actualizarHistorial();
    }

    @FXML
    void actualizarVista() {
        Monedero monedero = cmbMonedero.getValue();
        if (monedero == null) {
            lblSaldo.setText("Saldo: -");
            lblCliente.setText("Seleccione un monedero");
        } else {
            lblSaldo.setText(String.format("Saldo: %.2f", monedero.getSaldo()));
            lblCliente.setText(monedero.getPropietario().getNombre() + " · rango "
                    + monedero.getPropietario().getRango() + " · "
                    + monedero.getPropietario().getPuntosDisponibles() + " puntos disponibles");
        }
        actualizarHistorial();
    }

    @FXML
    void actualizarHistorial() {
        tblHistorial.setItems(FXCollections.observableArrayList(
                controller.obtenerHistorial(cmbMonedero.getValue(), cmbFiltro.getValue())));
        tblHistorial.refresh();
    }

    @FXML
    void volver() {
        app.openViewPrincipal();
    }

    private void informar(Transaccion t) {
        String mensaje = String.format("%s %s por %.2f%nComisión: %.2f%nPuntos ganados: %d",
                t.getTipo(), t.getCodigo(), t.getMonto(), t.getComision(), t.getPuntosGenerados());
        if (t.isSospechosa()) {
            mensaje += "\n\n⚠ Marcada como SOSPECHOSA (supera promedio + 2 desviaciones)";
        }
        Mensajes.info("Transacción exitosa", mensaje);
        txtMonto.clear();
        actualizarVista();
    }
}
