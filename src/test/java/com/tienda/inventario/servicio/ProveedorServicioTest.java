package com.tienda.inventario.servicio;

import com.tienda.inventario.dao.ProveedorDao;
import com.tienda.inventario.dto.ProveedorDTO;
import com.tienda.inventario.excepcion.NoEncontradoException;
import com.tienda.inventario.excepcion.ValidacionException;
import com.tienda.inventario.modelo.Proveedor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProveedorServicioTest {
    @Mock
    private ProveedorDao proveedorDao;

    @InjectMocks
    private ProveedorServicio servicio;

    @Test
    void crearDevuelveElProveedorCreado() {
        when(proveedorDao.insertar(any(Proveedor.class))).thenReturn(12);

        ProveedorDTO resultado = servicio.crear(new ProveedorDTO(0, "Suministros", "555-0101", "info@ejemplo.test"));

        assertEquals(12, resultado.getId());
        assertEquals("Suministros", resultado.getNombre());
        assertEquals("555-0101", resultado.getTelefono());
        assertEquals("info@ejemplo.test", resultado.getEmail());
        verify(proveedorDao).insertar(any(Proveedor.class));
    }

    @Test
    void obtenerDevuelveElProveedorExistente() {
        when(proveedorDao.buscarPorId(12))
                .thenReturn(new Proveedor(12, "Suministros", "555-0101", "info@ejemplo.test"));

        ProveedorDTO resultado = servicio.obtener(12);

        assertEquals(12, resultado.getId());
        assertEquals("Suministros", resultado.getNombre());
        assertEquals("555-0101", resultado.getTelefono());
        assertEquals("info@ejemplo.test", resultado.getEmail());
    }

    @Test
    void obtenerIdInexistenteLanzaNoEncontradoException() {
        when(proveedorDao.buscarPorId(99)).thenReturn(null);

        assertThrows(NoEncontradoException.class, () -> servicio.obtener(99));
    }

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