package com.tienda.inventario.servicio;

import com.tienda.inventario.dao.CategoriaDao;
import com.tienda.inventario.dto.CategoriaDTO;
import com.tienda.inventario.excepcion.NoEncontradoException;
import com.tienda.inventario.excepcion.ValidacionException;
import com.tienda.inventario.modelo.Categoria;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CategoriaServicio {
    @Inject
    private CategoriaDao categoriaDao;

    public List<CategoriaDTO> listar() {
        List<CategoriaDTO> categorias = new ArrayList<>();
        for (Categoria categoria : categoriaDao.listar()) {
            categorias.add(new CategoriaDTO(categoria.getId(), categoria.getNombre()));
        }
        return categorias;
    }

    public CategoriaDTO obtener(int id) {
        return convertir(obtenerCategoria(id));
    }

    public CategoriaDTO crear(CategoriaDTO categoriaDTO) {
        String nombre = validarNombre(categoriaDTO);
        int id = categoriaDao.insertar(new Categoria(0, nombre));
        return new CategoriaDTO(id, nombre);
    }

    public CategoriaDTO actualizar(int id, CategoriaDTO categoriaDTO) {
        String nombre = validarNombre(categoriaDTO);
        obtenerCategoria(id);
        categoriaDao.actualizar(new Categoria(id, nombre));
        return new CategoriaDTO(id, nombre);
    }

    public void borrar(int id) {
        obtenerCategoria(id);
        categoriaDao.borrar(id);
    }

    private Categoria obtenerCategoria(int id) {
        Categoria categoria = categoriaDao.buscarPorId(id);
        if (categoria == null) {
            throw new NoEncontradoException("No se encontró la categoría con id " + id + ".");
        }
        return categoria;
    }

    private String validarNombre(CategoriaDTO categoriaDTO) {
        if (categoriaDTO == null || categoriaDTO.getNombre() == null || categoriaDTO.getNombre().isBlank()) {
            throw new ValidacionException("El nombre de la categoría no puede estar vacío.");
        }
        String nombre = categoriaDTO.getNombre();
        if (nombre.length() > 100) {
            throw new ValidacionException("El nombre de la categoría no puede superar los 100 caracteres.");
        }
        return nombre;
    }

    private CategoriaDTO convertir(Categoria categoria) {
        return new CategoriaDTO(categoria.getId(), categoria.getNombre());
    }
}