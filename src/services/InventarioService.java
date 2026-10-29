package services;

import dao.InventarioDAO;
import dao.OrdenTallerDAO;

public class InventarioService {

    private final InventarioDAO inventarioDAO;
    private final OrdenTallerDAO ordenDAO;

    public InventarioService(InventarioDAO inventarioDAO, OrdenTallerDAO ordenDAO) {
        this.inventarioDAO = inventarioDAO;
        this.ordenDAO = ordenDAO;
    }

    public boolean validarDatosProducto(String nombre, int stock, double precio) {
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (stock < 0) return false;
        if (precio <= 0.0) return false;
        return true;
    }

    public boolean consumirRepuestoParaServicio(int idProducto, int stockActual, int cantidadUsada,
                                                double precioUnitario, int idOrden) {
        if (cantidadUsada <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (cantidadUsada > stockActual) {
            throw new IllegalStateException("Stock insuficiente para realizar el consumo");
        }
        if (idOrden <= 0) {
            throw new IllegalArgumentException("Debe asociarse a una orden válida");
        }

        double costoTotal = cantidadUsada * precioUnitario;
        boolean stockDescontado = inventarioDAO.restarStock(idProducto, cantidadUsada);
        if (stockDescontado) {
            return ordenDAO.agregarCostoRepuesto(idOrden, costoTotal);
        }
        return false;
    }
}