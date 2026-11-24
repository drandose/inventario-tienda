package com.tienda.inventario.servicio;

import com.tienda.inventario.dao.CategoriaDao;
import com.tienda.inventario.dto.CategoriaDTO;
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
}