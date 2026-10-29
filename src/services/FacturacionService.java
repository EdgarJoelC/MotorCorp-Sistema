package services;

import dao.OrdenTallerDAO;

public class FacturacionService {

    private final OrdenTallerDAO ordenDAO;

    public FacturacionService(OrdenTallerDAO ordenDAO) {
        this.ordenDAO = ordenDAO;
    }

    public double calcularTotal(double manoObra, double repuestos) {
        if (manoObra < 0 || repuestos < 0) {
            throw new IllegalArgumentException("Los importes no pueden ser negativos");
        }
        return manoObra + repuestos;
    }

    public double calcularVuelto(double total, double efectivoEntregado) {
        if (efectivoEntregado < total) {
            throw new IllegalArgumentException("El efectivo entregado es insuficiente");
        }
        return efectivoEntregado - total;
    }

    public boolean procesarCobro(int idOrden, String cliente, String vehiculo, String placa,
                                 double manoObra, double repuestos, String metodoPago) {
        if (idOrden <= 0) {
            throw new IllegalArgumentException("ID de orden inválido");
        }
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar un método de pago");
        }
        double total = calcularTotal(manoObra, repuestos);
        return ordenDAO.registrarCobroYArchivar(idOrden, cliente, vehiculo, placa, manoObra, repuestos, total, metodoPago);
    }
}