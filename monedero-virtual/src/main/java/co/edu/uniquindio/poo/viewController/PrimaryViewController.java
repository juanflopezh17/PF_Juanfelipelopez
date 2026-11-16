package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.App;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PrimaryViewController {

    private App app;

    @FXML
    private Label lblResumen;

    public void setApp(App app) {
        this.app = app;
        lblResumen.setText(App.sistema.getClientes().size() + " clientes · "
                + App.sistema.obtenerTodosLosMonederos().size() + " monederos · "
                + App.sistema.getProgramadas().size() + " transacciones programadas");
    }

    @FXML
    void abrirClientes() {
        app.openClientes();
    }

    @FXML
    void abrirTransacciones() {
        app.openTransacciones();
    }

    @FXML
    void abrirPuntos() {
        app.openPuntos();
    }

    @FXML
    void abrirProgramadas() {
        app.openProgramadas();
    }

    @FXML
    void abrirReportes() {
        app.openReportes();
    }

    @FXML
    void salir() {
        Platform.exit();
    }
}
