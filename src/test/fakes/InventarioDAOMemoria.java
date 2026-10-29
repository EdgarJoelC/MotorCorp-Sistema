package test.fakes;

import dao.InventarioDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventarioDAOMemoria extends InventarioDAO {

    private final Map<Integer, Integer> stockMemoria = new HashMap<>();
    private final Map<Integer, Double> preciosMemoria = new HashMap<>();
    private final Map<Integer, String> nombresMemoria = new HashMap<>();

    public void agregarProductoMemoria(int id, int stockInicial) {
        stockMemoria.put(id, stockInicial);
        nombresMemoria.put(id, "Producto " + id);
        preciosMemoria.put(id, 50.0);
    }

    public void agregarProductoMemoria(int id, String nombre, int stockInicial, double precio) {
        stockMemoria.put(id, stockInicial);
        nombresMemoria.put(id, nombre);
        preciosMemoria.put(id, precio);
    }

    @Override
    public boolean restarStock(int id, int cantidad) {
        int actual = stockMemoria.getOrDefault(id, 0);
        if (actual >= cantidad) {
            stockMemoria.put(id, actual - cantidad);
            return true;
        }
        return false;
    }

    @Override
    public boolean aumentarStock(int id, int cantidad) {
        stockMemoria.put(id, stockMemoria.getOrDefault(id, 0) + cantidad);
        return true;
    }

    @Override
    public boolean actualizarStock(int id, int nuevoStock) {
        stockMemoria.put(id, nuevoStock);
        return true;
    }

    public int obtenerStock(int id) {
        return stockMemoria.getOrDefault(id, 0);
    }

    @Override
    public List<Object[]> listar() {
        List<Object[]> lista = new ArrayList<>();
        for (Integer id : stockMemoria.keySet()) {
            lista.add(new Object[]{id, nombresMemoria.get(id), stockMemoria.get(id), preciosMemoria.get(id)});
        }
        return lista;
    }
}