package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.App;
import co.edu.uniquindio.poo.controller.PuntosController;
import co.edu.uniquindio.poo.model.Beneficio;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.PuntosInsuficientesException;
import co.edu.uniquindio.poo.model.Rango;
import co.edu.uniquindio.poo.model.RegistroNoEncontradoException;
import co.edu.uniquindio.poo.model.RegistroPuntos;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class PuntosViewController {

    private App app;
    private PuntosController controller;

    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private Label lblRango;
    @FXML private Label lblAcumulados;
    @FXML private Label lblDisponibles;
    @FXML private Label lblBeneficiosRango;
    @FXML private Label lblSiguienteRango;
    @FXML private ComboBox<Monedero> cmbMonederoBono;
    @FXML private ListView<RegistroPuntos> lstHistorialPuntos;
    @FXML private ListView<Beneficio> lstBeneficios;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void initialize() {
        controller = new PuntosController(App.sistema);
        cmbCliente.setItems(FXCollections.observableArrayList(controller.obtenerClientes()));
        mostrarCliente();
    }

    @FXML
    void mostrarCliente() {
        Cliente cliente = cmbCliente.getValue();
        if (cliente == null) {
            lblRango.setText("-");
            lblAcumulados.setText("-");
            lblDisponibles.setText("-");
            lblBeneficiosRango.setText("-");
            lblSiguienteRango.setText("");
            cmbMonederoBono.getItems().clear();
            lstHistorialPuntos.getItems().clear();
            lstBeneficios.getItems().clear();
            return;
        }
        Rango rango = cliente.getRango();
        lblRango.setText(rango.toString());
        lblAcumulados.setText(String.valueOf(cliente.getPuntosAcumulados()));
        lblDisponibles.setText(String.valueOf(cliente.getPuntosDisponibles()));
        lblBeneficiosRango.setText(String.format("%.0f%% menos comisión · %.0f%% puntos extra",
                rango.getDescuentoComision() * 100, rango.getBonoPuntos() * 100));
        if (rango.ordinal() < Rango.values().length - 1) {
            Rango siguiente = Rango.values()[rango.ordinal() + 1];
            lblSiguienteRango.setText("Faltan " + (siguiente.getPuntosMin() - cliente.getPuntosAcumulados())
                    + " puntos para " + siguiente);
        } else {
            lblSiguienteRango.setText("¡Rango máximo alcanzado!");
        }
        cmbMonederoBono.setItems(FXCollections.observableArrayList(cliente.getMonederos()));
        lstHistorialPuntos.setItems(FXCollections.observableArrayList(cliente.getHistorialPuntos()));
        lstBeneficios.setItems(FXCollections.observableArrayList(cliente.getBeneficios()));
    }

    @FXML
    void canjearDescuento() {
        try {
            controller.canjearDescuentoTransferencia(cmbCliente.getValue());
            canjeExitoso("10% de descuento en la comisión de tus próximas 5 transferencias");
        } catch (PuntosInsuficientesException e) {
            Mensajes.error("Puntos insuficientes", e.getMessage());
        } catch (RegistroNoEncontradoException | IllegalArgumentException e) {
            Mensajes.error("No se pudo canjear", e.getMessage());
        }
    }

    @FXML
    void canjearRetiros() {
        try {
            controller.canjearRetirosSinCargo(cmbCliente.getValue());
            canjeExitoso("Un mes sin comisión en los retiros");
        } catch (PuntosInsuficientesException e) {
            Mensajes.error("Puntos insuficientes", e.getMessage());
        } catch (RegistroNoEncontradoException | IllegalArgumentException e) {
            Mensajes.error("No se pudo canjear", e.getMessage());
        }
    }

    @FXML
    void canjearBono() {
        try {
            controller.canjearBonoSaldo(cmbCliente.getValue(), cmbMonederoBono.getValue());
            canjeExitoso("Se abonaron 50 al monedero " + cmbMonederoBono.getValue().getNombre());
        } catch (PuntosInsuficientesException e) {
            Mensajes.error("Puntos insuficientes", e.getMessage());
        } catch (RegistroNoEncontradoException | IllegalArgumentException e) {
            Mensajes.error("No se pudo canjear", e.getMessage());
        }
    }

    @FXML
    void volver() {
        app.openViewPrincipal();
    }

    private void canjeExitoso(String detalle) {
        Mensajes.info("Canje exitoso", detalle);
        mostrarCliente();
    }
}
