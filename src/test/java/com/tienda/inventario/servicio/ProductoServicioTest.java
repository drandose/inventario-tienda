package com.tienda.inventario.servicio;

import com.tienda.inventario.dao.CategoriaDao;
import com.tienda.inventario.dao.ProductoDao;
import com.tienda.inventario.dao.ProductoProveedorDao;
import com.tienda.inventario.dao.ProveedorDao;
import com.tienda.inventario.dto.ProductoDTO;
import com.tienda.inventario.excepcion.NoEncontradoException;
import com.tienda.inventario.excepcion.ValidacionException;
import com.tienda.inventario.modelo.Categoria;
import com.tienda.inventario.modelo.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServicioTest {
    @Mock
    private ProductoDao productoDao;

    @Mock
    private CategoriaDao categoriaDao;

    @Mock
    private ProveedorDao proveedorDao;

    @Mock
    private ProductoProveedorDao productoProveedorDao;

    @InjectMocks
    private ProductoServicio servicio;

    @Test
    void crearDevuelveElProductoCreado() {
        when(categoriaDao.buscarPorId(1)).thenReturn(new Categoria(1, "Periféricos"));
        when(productoDao.insertar(any(Producto.class))).thenReturn(23);
        when(productoDao.buscarPorId(23)).thenReturn(
                new Producto(23, "TEC-001", "Teclado", new BigDecimal("49.90"), 8, 1, "Periféricos"));

        ProductoDTO resultado = servicio.crear(producto("TEC-001", "Teclado", new BigDecimal("49.90"), 8));

        assertEquals(23, resultado.getId());
        assertEquals("TEC-001", resultado.getCodigo());
        assertEquals("Periféricos", resultado.getCategoriaNombre());
        verify(productoDao).insertar(any(Producto.class));
    }

    @Test
    void obtenerDevuelveElProductoExistente() {
        when(productoDao.buscarPorId(23)).thenReturn(
                new Producto(23, "TEC-001", "Teclado", BigDecimal.ONE, 8, 1, "Periféricos"));

        ProductoDTO resultado = servicio.obtener(23);

        assertEquals(23, resultado.getId());
        assertEquals("Teclado", resultado.getNombre());
        assertEquals("Periféricos", resultado.getCategoriaNombre());
    }

    @Test
    void obtenerIdInexistenteLanzaNoEncontradoException() {
        when(productoDao.buscarPorId(99)).thenReturn(null);

        assertThrows(NoEncontradoException.class, () -> servicio.obtener(99));
    }

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

    @Test
    void crearRechazaCategoriaInexistente() {
        when(categoriaDao.buscarPorId(1)).thenReturn(null);

        assertThrows(ValidacionException.class,
                () -> servicio.crear(producto("TEC-001", "Teclado", BigDecimal.ONE, 1)));
    }

    private ProductoDTO producto(String codigo, String nombre, BigDecimal precio, int stock) {
        return new ProductoDTO(0, codigo, nombre, precio, stock, 1, null);
    }
}