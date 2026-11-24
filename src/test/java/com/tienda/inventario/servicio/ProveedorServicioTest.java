package com.tienda.inventario.servicio;

import com.tienda.inventario.dto.ProveedorDTO;
import com.tienda.inventario.excepcion.ValidacionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ProveedorServicioTest {
    private final ProveedorServicio servicio = new ProveedorServicio();

    @Test
    void crearRechazaNombreVacio() {
        assertThrows(ValidacionException.class, () -> servicio.crear(new ProveedorDTO(0, "  ", null, null)));
    }

    @Test
    void crearRechazaEmailSinArroba() {
        assertThrows(ValidacionException.class,
                () -> servicio.crear(new ProveedorDTO(0, "Proveedor", null, "email-invalido")));
    }
}