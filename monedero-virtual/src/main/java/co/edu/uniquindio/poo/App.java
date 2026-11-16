package co.edu.uniquindio.poo;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Frecuencia;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.SistemaMonedero;
import co.edu.uniquindio.poo.model.TipoMonedero;
import co.edu.uniquindio.poo.model.TipoTransaccion;
import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.viewController.ClienteViewController;
import co.edu.uniquindio.poo.viewController.PrimaryViewController;
import co.edu.uniquindio.poo.viewController.ProgramadaViewController;
import co.edu.uniquindio.poo.viewController.PuntosViewController;
import co.edu.uniquindio.poo.viewController.ReporteViewController;
import co.edu.uniquindio.poo.viewController.TransaccionViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class App extends Application {

    public static SistemaMonedero sistema = new SistemaMonedero("Monedero Virtual UQ");

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Monedero Virtual con sistema de Puntos");
        inicializarData();
        openViewPrincipal();
    }

    public void openViewPrincipal() {
        PrimaryViewController controller = cargarVista("primary.fxml", 620, 560);
        controller.setApp(this);
    }

    public void openClientes() {
        ClienteViewController controller = cargarVista("cliente.fxml", 1100, 700);
        controller.setApp(this);
    }

    public void openTransacciones() {
        TransaccionViewController controller = cargarVista("transacciones.fxml", 1100, 700);
        controller.setApp(this);
    }

    public void openPuntos() {
        PuntosViewController controller = cargarVista("puntos.fxml", 1100, 700);
        controller.setApp(this);
    }

    public void openProgramadas() {
        ProgramadaViewController controller = cargarVista("programadas.fxml", 1100, 700);
        controller.setApp(this);
    }

    public void openReportes() {
        ReporteViewController controller = cargarVista("reportes.fxml", 1100, 700);
        controller.setApp(this);
    }


    private <T> T cargarVista(String fxml, double ancho, double alto) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root, ancho, alto));
            primaryStage.centerOnScreen();
            primaryStage.show();
            return loader.getController();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar la vista " + fxml, e);
        }
    }

    private void inicializarData() {
        try {
            Cliente ana = new Cliente("Ana Gómez", "1094000001", "ana.gomez@gmail.com", "3001234567");
            Cliente luis = new Cliente("Luis Pérez", "1094000002", "luis.perez@gmail.com", "3109876543");
            Cliente carla = new Cliente("Carla Ruiz", "1094000003", "carla.ruiz@gmail.com", "3205556677");
            sistema.registrarCliente(ana);
            sistema.registrarCliente(luis);
            sistema.registrarCliente(carla);

            Monedero anaAhorros = sistema.crearMonedero(ana.getDocumento(), "Ahorros", TipoMonedero.AHORROS);
            Monedero anaDiario = sistema.crearMonedero(ana.getDocumento(), "Gastos diarios", TipoMonedero.GASTOS_DIARIOS);
            Monedero luisPrincipal = sistema.crearMonedero(luis.getDocumento(), "Principal", TipoMonedero.GASTOS_DIARIOS);
            Monedero luisViajes = sistema.crearMonedero(luis.getDocumento(), "Viajes", TipoMonedero.PERSONALIZADO);
            Monedero carlaPrincipal = sistema.crearMonedero(carla.getDocumento(), "Principal", TipoMonedero.AHORROS);

            LocalDateTime hoy = LocalDateTime.now();

            conFecha(sistema.depositar(anaAhorros.getCodigo(), 1500), hoy.minusMonths(2).minusDays(10));
            conFecha(sistema.depositar(anaAhorros.getCodigo(), 1200), hoy.minusMonths(2).minusDays(3));
            conFecha(sistema.transferir(anaAhorros.getCodigo(), anaDiario.getCodigo(), 800), hoy.minusMonths(2));
            conFecha(sistema.retirar(anaDiario.getCodigo(), 250), hoy.minusMonths(1).minusDays(12));
            conFecha(sistema.depositar(anaAhorros.getCodigo(), 1800), hoy.minusMonths(1).minusDays(5));
            conFecha(sistema.transferir(anaAhorros.getCodigo(), carlaPrincipal.getCodigo(), 400), hoy.minusMonths(1));
            conFecha(sistema.retirar(anaDiario.getCodigo(), 300), hoy.minusDays(6));
            conFecha(sistema.depositar(anaAhorros.getCodigo(), 1000), hoy.minusDays(2));

            conFecha(sistema.depositar(luisPrincipal.getCodigo(), 20000), hoy.minusMonths(2).minusDays(8));
            conFecha(sistema.depositar(luisPrincipal.getCodigo(), 25000), hoy.minusMonths(1).minusDays(15));
            conFecha(sistema.transferir(luisPrincipal.getCodigo(), luisViajes.getCodigo(), 5000), hoy.minusMonths(1));
            conFecha(sistema.retirar(luisPrincipal.getCodigo(), 3000), hoy.minusDays(20));
            conFecha(sistema.depositar(luisPrincipal.getCodigo(), 15000), hoy.minusDays(4));

            conFecha(sistema.depositar(carlaPrincipal.getCodigo(), 300), hoy.minusDays(15));
            conFecha(sistema.retirar(carlaPrincipal.getCodigo(), 600), hoy.minusDays(3));

            sistema.programarTransaccion(TipoTransaccion.TRANSFERENCIA, 200, luisPrincipal.getCodigo(),
                    carlaPrincipal.getCodigo(), LocalDate.now().minusDays(7), Frecuencia.SEMANAL);
            sistema.programarTransaccion(TipoTransaccion.DEPOSITO, 500, anaAhorros.getCodigo(),
                    null, LocalDate.now().plusDays(1), Frecuencia.MENSUAL);
        } catch (Exception e) {
            System.out.println("Error cargando datos de ejemplo: " + e.getMessage());
        }
    }

    private void conFecha(Transaccion transaccion, LocalDateTime fecha) {
        transaccion.setFecha(fecha);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
