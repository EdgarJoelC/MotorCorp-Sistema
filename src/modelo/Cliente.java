package modelo;

public class Cliente {

    private int id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String celular;

    public Cliente() {
    }

    public Cliente(int id, String dni, String nombres, String apellidos, String celular) {
        this.id = id;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.celular = celular;
    }

    public Cliente(String dni, String nombres, String apellidos, String celular) {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.celular = celular;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }
}