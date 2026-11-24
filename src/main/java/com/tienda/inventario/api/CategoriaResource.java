package com.tienda.inventario.api;

import com.tienda.inventario.dto.CategoriaDTO;
import com.tienda.inventario.servicio.CategoriaServicio;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@RequestScoped
@Path("/categorias")
public class CategoriaResource {
    @Inject
    private CategoriaServicio categoriaServicio;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CategoriaDTO> listar() {
        return categoriaServicio.listar();
    }
}