package com.tienda.inventario.servicio;

import com.tienda.inventario.dao.CategoriaDao;
import com.tienda.inventario.dto.CategoriaDTO;
import com.tienda.inventario.excepcion.NoEncontradoException;
import com.tienda.inventario.excepcion.ValidacionException;
import com.tienda.inventario.modelo.Categoria;
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
class CategoriaServicioTest {
    @Mock
    private CategoriaDao categoriaDao;

    @InjectMocks
    private CategoriaServicio servicio;

    @Test
    void crearDevuelveLaCategoriaCreada() {
        when(categoriaDao.insertar(any(Categoria.class))).thenReturn(7);

        CategoriaDTO resultado = servicio.crear(new CategoriaDTO(0, "Informática"));

        assertEquals(7, resultado.getId());
        assertEquals("Informática", resultado.getNombre());
        verify(categoriaDao).insertar(any(Categoria.class));
    }

    @Test
    void obtenerDevuelveLaCategoriaExistente() {
        when(categoriaDao.buscarPorId(7)).thenReturn(new Categoria(7, "Informática"));

        CategoriaDTO resultado = servicio.obtener(7);

        assertEquals(7, resultado.getId());
        assertEquals("Informática", resultado.getNombre());
    }

    @Test
    void obtenerIdInexistenteLanzaNoEncontradoException() {
        when(categoriaDao.buscarPorId(99)).thenReturn(null);

        assertThrows(NoEncontradoException.class, () -> servicio.obtener(99));
    }

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