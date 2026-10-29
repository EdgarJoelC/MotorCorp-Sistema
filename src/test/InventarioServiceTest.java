package test;

import org.junit.Before;
import org.junit.Test;
import services.InventarioService;
import test.fakes.InventarioDAOMemoria;
import test.fakes.OrdenTallerDAOMemoria;
import static org.junit.Assert.*;

public class InventarioServiceTest {

    private InventarioService inventarioService;
    private InventarioDAOMemoria inventarioDAOMemoria;
    private OrdenTallerDAOMemoria ordenDAOMemoria;

    @Before
    public void setUp() {
        inventarioDAOMemoria = new InventarioDAOMemoria();
        ordenDAOMemoria = new OrdenTallerDAOMemoria();
        inventarioService = new InventarioService(inventarioDAOMemoria, ordenDAOMemoria);

        inventarioDAOMemoria.agregarProductoMemoria(101, 10);
        ordenDAOMemoria.registrar(1, 1, "Toyota Yaris", "AEA-312", "Frenos", 50.00);
    }

    @Test
    public void testConsumoRepuestoDisminuyeStockYAcumulaCostoEnOrden() {
        boolean resultado = inventarioService.consumirRepuestoParaServicio(101, 10, 2, 45.00, 1);
        assertTrue(resultado);
        assertEquals(8, inventarioDAOMemoria.obtenerStock(101));
        assertEquals(90.00, ordenDAOMemoria.buscarPorId(1).repuestos, 0.001);
    }

    @Test(expected = IllegalStateException.class)
    public void testConsumoRepuestoFallaSiSuperaStockDisponible() {
        inventarioService.consumirRepuestoParaServicio(101, 10, 15, 45.00, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConsumoRepuestoFallaConCantidadCeroONegativa() {
        inventarioService.consumirRepuestoParaServicio(101, 10, 0, 45.00, 1);
    }

    @Test
    public void testValidacionProductoCamposValidos() {
        assertTrue(inventarioService.validarDatosProducto("Batería Bosch", 5, 280.00));
        assertFalse(inventarioService.validarDatosProducto("", 5, 280.00));
        assertFalse(inventarioService.validarDatosProducto("Aceite", -1, 50.00));
        assertFalse(inventarioService.validarDatosProducto("Aceite", 10, 0.00));
    }
}