package modelo;

public class OrdenTaller {

    private int id;
    private int idCliente;
    private int idMecanico;
    private String descripcionFalla;
    private String estado;
    private double costoEstimado;

    public OrdenTaller() {
    }

    public OrdenTaller(int id, int idCliente, int idMecanico, String descripcionFalla, String estado, double costoEstimado) {
        this.id = id;
        this.idCliente = idCliente;
        this.idMecanico = idMecanico;
        this.descripcionFalla = descripcionFalla;
        this.estado = estado;
        this.costoEstimado = costoEstimado;
    }

    public OrdenTaller(int idCliente, int idMecanico, String descripcionFalla, String estado, double costoEstimado) {
        this.idCliente = idCliente;
        this.idMecanico = idMecanico;
        this.descripcionFalla = descripcionFalla;
        this.estado = estado;
        this.costoEstimado = costoEstimado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdMecanico() {
        return idMecanico;
    }

    public void setIdMecanico(int idMecanico) {
        this.idMecanico = idMecanico;
    }

    public String getDescripcionFalla() {
        return descripcionFalla;
    }

    public void setDescripcionFalla(String descripcionFalla) {
        this.descripcionFalla = descripcionFalla;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getCostoEstimado() {
        return costoEstimado;
    }

    public void setCostoEstimado(double costoEstimado) {
        this.costoEstimado = costoEstimado;
    }
}