package co.edu.uniquindio.poo.model;


public interface INotificador {

    void enviar(Cliente cliente, Notificacion notificacion);

    String getCanal();
}
