package test;

import dao.ClienteDAO;
import org.junit.Before;
import org.junit.Test;
import services.ClienteService;
import static org.junit.Assert.*;

public class ClienteServiceTest {

    private ClienteService clienteService;

    @Before
    public void setUp() {
        ClienteDAO mockDAO = new ClienteDAO();
        clienteService = new ClienteService(mockDAO);
    }

    @Test
    public void testValidacionDniOchoDigitosExactos() {
        assertTrue(clienteService.validarDNI("41241511"));
        assertFalse(clienteService.validarDNI("4124151"));
        assertFalse(clienteService.validarDNI("412415119"));
        assertFalse(clienteService.validarDNI("ABC41511"));
    }

    @Test
    public void testValidacionTelefonoNueveDigitos() {
        assertTrue(clienteService.validarTelefono("991234567"));
        assertTrue(clienteService.validarTelefono(""));
        assertFalse(clienteService.validarTelefono("99123456"));
        assertFalse(clienteService.validarTelefono("9912345678"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegistroClienteLanzaErrorSiDniInvalido() {
        clienteService.registrarCliente("Pedro Castillo", "123", "999888777", "Kia Rio", "XYZ-123");
    }
}