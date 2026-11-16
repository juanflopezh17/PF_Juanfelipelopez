package co.edu.uniquindio.poo.model;


public class NotificadorEmail implements INotificador {

    @Override
    public void enviar(Cliente cliente, Notificacion notificacion) {
        if (cliente.getCorreo().isBlank()) {
            return;
        }
        System.out.println("[EMAIL -> " + cliente.getCorreo() + "] " + notificacion.getMensaje());
    }

    @Override
    public String getCanal() {
        return "EMAIL";
    }
}
