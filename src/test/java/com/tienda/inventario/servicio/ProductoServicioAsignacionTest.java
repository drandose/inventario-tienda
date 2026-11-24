package com.tienda.inventario.servicio;

import com.tienda.inventario.dao.ProductoDao;
import com.tienda.inventario.dao.ProductoProveedorDao;
import com.tienda.inventario.dao.ProveedorDao;
import com.tienda.inventario.excepcion.NoEncontradoException;
import com.tienda.inventario.modelo.Producto;
import com.tienda.inventario.modelo.Proveedor;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductoServicioAsignacionTest {
    @Test
    void asignarProveedoresValidaAntesDeBorrarLasRelacionesActuales() throws Exception {
        ProductoServicio servicio = new ProductoServicio();
        ProductoProveedorDaoFalso relaciones = new ProductoProveedorDaoFalso();
        asignar(servicio, "productoDao", new ProductoDaoFalso());
        asignar(servicio, "proveedorDao", new ProveedorDaoFalso());
        asignar(servicio, "productoProveedorDao", relaciones);

        assertThrows(NoEncontradoException.class, () -> servicio.asignarProveedores(1, List.of(10, 99)));
        assertEquals(0, relaciones.borrados);
        assertEquals(List.of(), relaciones.insertados);
    }

    @Test
    void asignarProveedoresBorraEInsertaUnaVezValidadasLasReferencias() throws Exception {
        ProductoServicio servicio = new ProductoServicio();
        ProductoProveedorDaoFalso relaciones = new ProductoProveedorDaoFalso();
        asignar(servicio, "productoDao", new ProductoDaoFalso());
        asignar(servicio, "proveedorDao", new ProveedorDaoFalso());
        asignar(servicio, "productoProveedorDao", relaciones);

        servicio.asignarProveedores(1, List.of(10, 11));

        assertEquals(1, relaciones.borrados);
        assertEquals(List.of("1:10", "1:11"), relaciones.insertados);
    }

    @Test
    void asignarProveedoresEsTransaccional() throws NoSuchMethodException {
        assertNotNull(ProductoServicio.class
                .getMethod("asignarProveedores", int.class, List.class)
                .getAnnotation(Transactional.class));
    }

    private void asignar(ProductoServicio servicio, String nombreCampo, Object valor) throws Exception {
        Field field = ProductoServicio.class.getDeclaredField(nombreCampo);
        field.setAccessible(true);
        field.set(servicio, valor);
    }

    private static class ProductoDaoFalso extends ProductoDao {
        @Override
        public Producto buscarPorId(int id) {
            return id == 1 ? new Producto(1, "COD-1", "Producto", BigDecimal.ONE, 1, 1) : null;
        }
    }

    private static class ProveedorDaoFalso extends ProveedorDao {
        @Override
        public Proveedor buscarPorId(int id) {
            return id == 10 || id == 11 ? new Proveedor(id, "Proveedor", null, null) : null;
        }
    }

    private static class ProductoProveedorDaoFalso extends ProductoProveedorDao {
        private int borrados;
        private final java.util.ArrayList<String> insertados = new java.util.ArrayList<>();

        @Override
        public void borrarPorProducto(int productoId) {
            borrados++;
        }

        @Override
        public void insertar(int productoId, int proveedorId) {
            insertados.add(productoId + ":" + proveedorId);
        }
    }
}