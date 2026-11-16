package co.edu.uniquindio.poo.model;

public class NotificadorWhatsApp implements INotificador {

    @Override
    public void enviar(Cliente cliente, Notificacion notificacion) {
        if (cliente.getTelefono().isBlank()) {
            return;
        }
        System.out.println("[WHATSAPP -> " + cliente.getTelefono() + "] " + notificacion.getMensaje());
    }

    @Override
    public String getCanal() {
        return "WHATSAPP";
    }
}
