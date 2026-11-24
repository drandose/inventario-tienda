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

class CategoriaDaoTest extends DaoTestSupport {
    private CategoriaDao categoriaDao;

    @BeforeEach
    void crearDao() {
        categoriaDao = new CategoriaDao(dataSource);
    }

    @Test
    void insertarBuscarListarActualizarYBorrar() {
        int id = categoriaDao.insertar(new Categoria(0, "Informática"));

        assertEquals(new Categoria(id, "Informática").getNombre(), categoriaDao.buscarPorId(id).getNombre());
        assertEquals(List.of(id), categoriaDao.listar().stream().map(Categoria::getId).toList());

        categoriaDao.actualizar(new Categoria(id, "Periféricos"));
        assertEquals("Periféricos", categoriaDao.buscarPorId(id).getNombre());

        categoriaDao.borrar(id);
        assertNull(categoriaDao.buscarPorId(id));
        assertEquals(List.of(), categoriaDao.listar());
    }

    @Test
    void insertarNombreRepetidoLanzaConflictoException() {
        categoriaDao.insertar(new Categoria(0, "Informática"));

        assertThrows(ConflictoException.class, () -> categoriaDao.insertar(new Categoria(0, "Informática")));
    }

    @Test
    void borrarCategoriaConProductosLanzaConflictoException() {
        int categoriaId = categoriaDao.insertar(new Categoria(0, "Informática"));
        new ProductoDao(dataSource).insertar(
                new Producto(0, "INF-001", "Portátil", new BigDecimal("799.99"), 4, categoriaId));

        assertThrows(ConflictoException.class, () -> categoriaDao.borrar(categoriaId));
    }

    @Test
    void buscarIdInexistenteDevuelveNull() {
        assertNull(categoriaDao.buscarPorId(999));
    }
}