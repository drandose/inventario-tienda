package com.tienda.inventario.dao;

import com.tienda.inventario.modelo.Proveedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProveedorDaoTest extends DaoTestSupport {
    private ProveedorDao proveedorDao;

    @BeforeEach
    void crearDao() {
        proveedorDao = new ProveedorDao(dataSource);
    }

    @Test
    void insertarBuscarListarActualizarYBorrar() {
        int id = proveedorDao.insertar(new Proveedor(0, "Suministros", "555-0101", "info@ejemplo.test"));

        Proveedor encontrado = proveedorDao.buscarPorId(id);
        assertEquals("Suministros", encontrado.getNombre());
        assertEquals("555-0101", encontrado.getTelefono());
        assertEquals("info@ejemplo.test", encontrado.getEmail());
        assertEquals(List.of(id), proveedorDao.listar().stream().map(Proveedor::getId).toList());

        proveedorDao.actualizar(new Proveedor(id, "Suministros actualizados", "555-0102", "ventas@ejemplo.test"));
        Proveedor actualizado = proveedorDao.buscarPorId(id);
        assertEquals("Suministros actualizados", actualizado.getNombre());
        assertEquals("555-0102", actualizado.getTelefono());
        assertEquals("ventas@ejemplo.test", actualizado.getEmail());

        proveedorDao.borrar(id);
        assertNull(proveedorDao.buscarPorId(id));
        assertEquals(List.of(), proveedorDao.listar());
    }

    @Test
    void buscarIdInexistenteDevuelveNull() {
        assertNull(proveedorDao.buscarPorId(999));
    }
}