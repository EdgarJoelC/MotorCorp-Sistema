package test.fakes;

import dao.OrdenTallerDAO;
import java.util.ArrayList;
import java.util.List;

public class OrdenTallerDAOMemoria extends OrdenTallerDAO {

    public static class OrdenFake {
        public int id;
        public int idCliente;
        public int idMecanico;
        public String vehiculo;
        public String placa;
        public String problema;
        public String estado;
        public double manoObra;
        public double repuestos;

        public OrdenFake(int id, int idCliente, int idMecanico, String vehiculo, String placa, String problema, double manoObra) {
            this.id = id;
            this.idCliente = idCliente;
            this.idMecanico = idMecanico;
            this.vehiculo = vehiculo;
            this.placa = placa;
            this.problema = problema;
            this.estado = "Recepcionado";
            this.manoObra = manoObra;
            this.repuestos = 0.0;
        }
    }

    private final List<OrdenFake> baseDatosMemoria = new ArrayList<>();
    private int contadorId = 1;

    @Override
    public boolean registrar(int idCliente, int idMecanico, String vehiculo, String placa, String descripcionProblema, double costoManoObra) {
        baseDatosMemoria.add(new OrdenFake(contadorId++, idCliente, idMecanico, vehiculo, placa, descripcionProblema, costoManoObra));
        return true;
    }

    @Override
    public boolean agregarCostoRepuesto(int idOrden, double montoRepuesto) {
        for (OrdenFake o : baseDatosMemoria) {
            if (o.id == idOrden) {
                o.repuestos += montoRepuesto;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean registrarCobroYArchivar(int idOrden, String cliente, String vehiculo, String placa, double manoObra, double repuestos, double total, String metodoPago) {
        for (OrdenFake o : baseDatosMemoria) {
            if (o.id == idOrden) {
                o.estado = "Entregado";
                return true;
            }
        }
        return false;
    }

    public OrdenFake buscarPorId(int id) {
        for (OrdenFake o : baseDatosMemoria) {
            if (o.id == id) return o;
        }
        return null;
    }
}