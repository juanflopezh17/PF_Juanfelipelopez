package co.edu.uniquindio.poo.model;


public abstract class Persona {

    private String nombre;
    private String documento;
    private String correo;
    private String telefono;

    public Persona(String nombre, String documento, String correo, String telefono) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }
        if (correo != null && !correo.isBlank() && !correo.contains("@")) {
            throw new IllegalArgumentException("El correo no es válido");
        }
        this.nombre = nombre.trim();
        this.documento = documento.trim();
        this.correo = correo == null ? "" : correo.trim();
        this.telefono = telefono == null ? "" : telefono.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        this.nombre = nombre.trim();
    }

    public String getDocumento() {
        return documento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        if (correo != null && !correo.isBlank() && !correo.contains("@")) {
            throw new IllegalArgumentException("El correo no es válido");
        }
        this.correo = correo == null ? "" : correo.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono == null ? "" : telefono.trim();
    }

    @Override
    public String toString() {
        return nombre + " (" + documento + ")";
    }
}
