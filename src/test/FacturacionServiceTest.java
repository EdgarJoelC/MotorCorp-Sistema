package test;

import org.junit.Before;
import org.junit.Test;
import services.FacturacionService;
import test.fakes.OrdenTallerDAOMemoria;
import static org.junit.Assert.*;

public class FacturacionServiceTest {

    private FacturacionService facturacionService;
    private OrdenTallerDAOMemoria ordenDAOMemoria;

    @Before
    public void setUp() {
        ordenDAOMemoria = new OrdenTallerDAOMemoria();
        facturacionService = new FacturacionService(ordenDAOMemoria);
        ordenDAOMemoria.registrar(1, 1, "Toyota Yaris", "AEA-312", "Frenos", 80.00);
    }

    @Test
    public void testCalcularTotalSumaManoObraYRepuestosCorrectamente() {
        double manoObra = 80.00;
        double repuestos = 45.00;
        double esperado = 125.00;
        assertEquals(esperado, facturacionService.calcularTotal(manoObra, repuestos), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalcularTotalLanzaExcepcionConValoresNegativos() {
        facturacionService.calcularTotal(-10.0, 50.0);
    }

    @Test
    public void testCalcularVueltoExacto() {
        double total = 125.00;
        double efectivo = 150.00;
        double esperado = 25.00;
        assertEquals(esperado, facturacionService.calcularVuelto(total, efectivo), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalcularVueltoFallaSiEfectivoEsMenor() {
        facturacionService.calcularVuelto(100.00, 80.00);
    }

    @Test
    public void testProcesarCobroArchivaServicioExitosamente() {
        boolean cobrado = facturacionService.procesarCobro(1, "Carlos Gomez", "Toyota Yaris", "AEA-312", 80.00, 45.00, "Efectivo");
        assertTrue(cobrado);
        assertEquals("Entregado", ordenDAOMemoria.buscarPorId(1).estado);
    }
}