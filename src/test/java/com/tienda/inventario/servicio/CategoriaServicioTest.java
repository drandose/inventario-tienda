package com.tienda.inventario.servicio;

import com.tienda.inventario.dto.CategoriaDTO;
import com.tienda.inventario.excepcion.ValidacionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoriaServicioTest {
    private final CategoriaServicio servicio = new CategoriaServicio();

    @Test
    void crearRechazaNombreVacio() {
        assertThrows(ValidacionException.class,
                () -> servicio.crear(new CategoriaDTO(0, "   ")));
    }

    @Test
    void crearRechazaNombreDeMasDeCienCaracteres() {
        assertThrows(ValidacionException.class,
                () -> servicio.crear(new CategoriaDTO(0, "a".repeat(101))));
    }
}