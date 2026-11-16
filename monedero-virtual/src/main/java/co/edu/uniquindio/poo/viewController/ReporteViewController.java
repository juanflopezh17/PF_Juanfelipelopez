package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.App;
import co.edu.uniquindio.poo.controller.ReporteController;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Notificacion;
import co.edu.uniquindio.poo.model.RegistroNoEncontradoException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class ReporteViewController {

    private App app;
    private ReporteController controller;

    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private TextArea txtReporte;
    @FXML private Label lblNotificaciones;
    @FXML private ListView<Notificacion> lstNotificaciones;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void initialize() {
        controller = new ReporteController(App.sistema);
        cmbCliente.setItems(FXCollections.observableArrayList(controller.obtenerClientes()));
        txtReporte.setEditable(false);
    }

    @FXML
    void mostrarCliente() {
        Cliente cliente = cmbCliente.getValue();
        txtReporte.clear();
        if (cliente == null) {
            lstNotificaciones.getItems().clear();
            lblNotificaciones.setText("Alertas");
            return;
        }
        lblNotificaciones.setText("Alertas de " + cliente.getNombre() + " (" + cliente.getNotificaciones().size() + ")");
        lstNotificaciones.setItems(FXCollections.observableArrayList(cliente.getNotificaciones()));
    }

    @FXML
    void generarReporte() {
        try {
            txtReporte.setText(controller.generarReporte(cmbCliente.getValue()));
        } catch (RegistroNoEncontradoException | IllegalArgumentException e) {
            Mensajes.error("Reporte", e.getMessage());
        }
    }

    @FXML
    void analizarAtipicas() {
        try {
            txtReporte.setText(controller.analizarAtipicas(cmbCliente.getValue()));
        } catch (IllegalArgumentException e) {
            Mensajes.error("Análisis", e.getMessage());
        }
    }

    @FXML
    void volver() {
        app.openViewPrincipal();
    }
}
