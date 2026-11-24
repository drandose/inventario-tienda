package com.tienda.inventario.servicio;

import com.tienda.inventario.dto.ProductoDTO;
import com.tienda.inventario.excepcion.ValidacionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductoServicioTest {
    private final ProductoServicio servicio = new ProductoServicio();

    @Test
    void crearRechazaCodigoVacio() {
        assertThrows(ValidacionException.class, () -> servicio.crear(producto("  ", "Teclado", BigDecimal.ONE, 1)));
    }

    @Test
    void crearRechazaNombreVacio() {
        assertThrows(ValidacionException.class, () -> servicio.crear(producto("TEC-001", "", BigDecimal.ONE, 1)));
    }

    @Test
    void crearRechazaPrecioNulo() {
        assertThrows(ValidacionException.class, () -> servicio.crear(producto("TEC-001", "Teclado", null, 1)));
    }

    @Test
    void crearRechazaPrecioNoPositivo() {
        assertThrows(ValidacionException.class,
                () -> servicio.crear(producto("TEC-001", "Teclado", BigDecimal.ZERO, 1)));
    }

    @Test
    void crearRechazaStockNegativo() {
        assertThrows(ValidacionException.class,
                () -> servicio.crear(producto("TEC-001", "Teclado", BigDecimal.ONE, -1)));
    }

    private ProductoDTO producto(String codigo, String nombre, BigDecimal precio, int stock) {
        return new ProductoDTO(0, codigo, nombre, precio, stock, 1, null);
    }
}