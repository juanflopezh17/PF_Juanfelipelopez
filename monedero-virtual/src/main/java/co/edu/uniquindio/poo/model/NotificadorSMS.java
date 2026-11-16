package co.edu.uniquindio.poo.model;

public class NotificadorSMS implements INotificador {

    @Override
    public void enviar(Cliente cliente, Notificacion notificacion) {
        if (cliente.getTelefono().isBlank()) {
            return;
        }
        System.out.println("[SMS -> " + cliente.getTelefono() + "] " + notificacion.getMensaje());
    }

    @Override
    public String getCanal() {
        return "SMS";
    }
}
