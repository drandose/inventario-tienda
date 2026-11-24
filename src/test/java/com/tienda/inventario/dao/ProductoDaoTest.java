package com.tienda.inventario.dao;

import com.tienda.inventario.excepcion.ConflictoException;
import com.tienda.inventario.modelo.Categoria;
import com.tienda.inventario.modelo.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductoDaoTest extends DaoTestSupport {
    private ProductoDao productoDao;
    private int categoriaId;

    @BeforeEach
    void crearDao() {
        productoDao = new ProductoDao(dataSource);
        categoriaId = new CategoriaDao(dataSource).insertar(new Categoria(0, "Informática"));
    }

    @Test
    void insertarBuscarListarActualizarYBorrar() {
        int id = productoDao.insertar(producto("INF-001", "Portátil", new BigDecimal("799.99"), 4));

        Producto encontrado = productoDao.buscarPorId(id);
        assertEquals("INF-001", encontrado.getCodigo());
        assertEquals("Portátil", encontrado.getNombre());
        assertEquals("Informática", encontrado.getCategoriaNombre());
        assertEquals(List.of(id), productoDao.listar().stream().map(Producto::getId).toList());

        productoDao.actualizar(new Producto(id, "INF-001", "Portátil actualizado",
                new BigDecimal("899.99"), 6, categoriaId));
        assertEquals("Portátil actualizado", productoDao.buscarPorId(id).getNombre());
        assertEquals(new BigDecimal("899.99"), productoDao.buscarPorId(id).getPrecio());

        productoDao.borrar(id);
        assertNull(productoDao.buscarPorId(id));
        assertEquals(List.of(), productoDao.listar());
    }

    @Test
    void insertarCodigoRepetidoLanzaConflictoException() {
        productoDao.insertar(producto("INF-001", "Portátil", BigDecimal.ONE, 1));

        assertThrows(ConflictoException.class,
                () -> productoDao.insertar(producto("INF-001", "Otro producto", BigDecimal.ONE, 1)));
    }

    @Test
    void buscarIdInexistenteDevuelveNull() {
        assertNull(productoDao.buscarPorId(999));
    }

    private Producto producto(String codigo, String nombre, BigDecimal precio, int stock) {
        return new Producto(0, codigo, nombre, precio, stock, categoriaId);
    }
}