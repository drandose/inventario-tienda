package com.tienda.inventario.dao;

import com.tienda.inventario.modelo.Categoria;
import com.tienda.inventario.modelo.Producto;
import com.tienda.inventario.modelo.Proveedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductoProveedorDaoTest extends DaoTestSupport {
    private ProductoProveedorDao productoProveedorDao;
    private int productoId;
    private int proveedorId;

    @BeforeEach
    void prepararRelaciones() {
        productoProveedorDao = new ProductoProveedorDao(dataSource);
        int categoriaId = new CategoriaDao(dataSource).insertar(new Categoria(0, "Informática"));
        productoId = new ProductoDao(dataSource).insertar(
                new Producto(0, "INF-001", "Portátil", BigDecimal.ONE, 1, categoriaId));
        proveedorId = new ProveedorDao(dataSource).insertar(new Proveedor(0, "Suministros", null, null));
    }

    @Test
    void insertarListarYBorrarRelacionesDeProducto() {
        productoProveedorDao.insertar(productoId, proveedorId);

        List<Proveedor> proveedores = productoProveedorDao.listarProveedoresDeProducto(productoId);
        assertEquals(1, proveedores.size());
        assertEquals(proveedorId, proveedores.getFirst().getId());

        productoProveedorDao.borrarPorProducto(productoId);
        assertEquals(List.of(), productoProveedorDao.listarProveedoresDeProducto(productoId));
    }
}