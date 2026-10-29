package services;

import dao.ClienteDAO;

public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public boolean validarDNI(String dni) {
        return dni != null && dni.matches("^\\d{8}$");
    }

    public boolean validarTelefono(String telefono) {
        return telefono != null && (telefono.isEmpty() || telefono.matches("^\\d{9}$"));
    }

    public boolean registrarCliente(String nombre, String dni, String telefono, String vehiculo, String placa) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (!validarDNI(dni)) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos");
        }
        if (!validarTelefono(telefono)) {
            throw new IllegalArgumentException("El teléfono debe tener 9 dígitos numéricos");
        }
        return clienteDAO.registrar(nombre.trim(), dni.trim(), telefono.trim(), vehiculo.trim(), placa.trim());
    }
}