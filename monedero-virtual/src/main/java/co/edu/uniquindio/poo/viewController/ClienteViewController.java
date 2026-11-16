package co.edu.uniquindio.poo.viewController;

import co.edu.uniquindio.poo.App;
import co.edu.uniquindio.poo.controller.ClienteController;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.RegistroNoEncontradoException;
import co.edu.uniquindio.poo.model.TipoMonedero;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ClienteViewController {

    private App app;
    private ClienteController controller;
    private Cliente clienteSeleccionado;

    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;

    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, String> colDocumento;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colRango;
    @FXML private TableColumn<Cliente, String> colPuntos;
    @FXML private TableColumn<Cliente, String> colSaldo;

    @FXML private Label lblClienteSeleccionado;
    @FXML private TextField txtNombreMonedero;
    @FXML private ComboBox<TipoMonedero> cmbTipoMonedero;
    @FXML private TableView<Monedero> tblMonederos;
    @FXML private TableColumn<Monedero, String> colCodigoMonedero;
    @FXML private TableColumn<Monedero, String> colNombreMonedero;
    @FXML private TableColumn<Monedero, String> colTipoMonedero;
    @FXML private TableColumn<Monedero, String> colSaldoMonedero;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void initialize() {
        controller = new ClienteController(App.sistema);

        colDocumento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDocumento()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo()));
        colTelefono.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefono()));
        colRango.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRango().toString()));
        colPuntos.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPuntosAcumulados())));
        colSaldo.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getSaldoTotal())));

        colCodigoMonedero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colNombreMonedero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colTipoMonedero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().toString()));
        colSaldoMonedero.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getSaldo())));

        cmbTipoMonedero.setItems(FXCollections.observableArrayList(TipoMonedero.values()));

        tblClientes.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, nuevo) -> mostrarCliente(nuevo));

        refrescarClientes();
    }

    @FXML
    void agregarCliente() {
        try {
            controller.agregarCliente(txtDocumento.getText(), txtNombre.getText(),
                    txtCorreo.getText(), txtTelefono.getText());
            Mensajes.info("Cliente registrado", "El cliente se registró correctamente");
            limpiarCampos();
            refrescarClientes();
        } catch (IllegalArgumentException e) {
            Mensajes.error("Datos inválidos", e.getMessage());
        }
    }

    @FXML
    void actualizarCliente() {
        if (clienteSeleccionado == null) {
            Mensajes.advertencia("Actualizar", "Seleccione un cliente de la tabla");
            return;
        }
        try {
            controller.actualizarCliente(clienteSeleccionado.getDocumento(), txtNombre.getText(),
                    txtCorreo.getText(), txtTelefono.getText());
            Mensajes.info("Cliente actualizado", "Los datos se actualizaron (el documento no se puede cambiar)");
            refrescarClientes();
        } catch (RegistroNoEncontradoException | IllegalArgumentException e) {
            Mensajes.error("No se pudo actualizar", e.getMessage());
        }
    }

    @FXML
    void eliminarCliente() {
        if (clienteSeleccionado == null) {
            Mensajes.advertencia("Eliminar", "Seleccione un cliente de la tabla");
            return;
        }
        if (!Mensajes.confirmar("Eliminar", "¿Eliminar a " + clienteSeleccionado.getNombre() + "?")) {
            return;
        }
        try {
            controller.eliminarCliente(clienteSeleccionado.getDocumento());
            limpiarCampos();
            refrescarClientes();
        } catch (RegistroNoEncontradoException | IllegalStateException e) {
            Mensajes.error("No se pudo eliminar", e.getMessage());
        }
    }

    @FXML
    void limpiarCampos() {
        tblClientes.getSelectionModel().clearSelection();
        txtDocumento.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtDocumento.setDisable(false);
        clienteSeleccionado = null;
        mostrarMonederos();
    }

    @FXML
    void crearMonedero() {
        if (clienteSeleccionado == null) {
            Mensajes.advertencia("Monedero", "Seleccione primero un cliente de la tabla");
            return;
        }
        try {
            Monedero monedero = controller.crearMonedero(clienteSeleccionado.getDocumento(),
                    txtNombreMonedero.getText(), cmbTipoMonedero.getValue());
            Mensajes.info("Monedero creado", "Se creó el monedero " + monedero.getCodigo());
            txtNombreMonedero.clear();
            cmbTipoMonedero.getSelectionModel().clearSelection();
            mostrarMonederos();
        } catch (RegistroNoEncontradoException | IllegalArgumentException e) {
            Mensajes.error("No se pudo crear el monedero", e.getMessage());
        }
    }

    @FXML
    void volver() {
        app.openViewPrincipal();
    }

    private void mostrarCliente(Cliente cliente) {
        clienteSeleccionado = cliente;
        if (cliente != null) {
            txtDocumento.setText(cliente.getDocumento());
            txtNombre.setText(cliente.getNombre());
            txtCorreo.setText(cliente.getCorreo());
            txtTelefono.setText(cliente.getTelefono());
            txtDocumento.setDisable(true);
        }
        mostrarMonederos();
    }

    private void mostrarMonederos() {
        if (clienteSeleccionado == null) {
            lblClienteSeleccionado.setText("Monederos (seleccione un cliente)");
            tblMonederos.getItems().clear();
            return;
        }
        lblClienteSeleccionado.setText("Monederos de " + clienteSeleccionado.getNombre());
        tblMonederos.setItems(FXCollections.observableArrayList(clienteSeleccionado.getMonederos()));
    }

    private void refrescarClientes() {
        tblClientes.setItems(FXCollections.observableArrayList(controller.obtenerClientes()));
        tblClientes.refresh();
    }
}
