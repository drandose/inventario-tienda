package com.tienda.inventario.api;

import com.tienda.inventario.dto.CategoriaDTO;
import com.tienda.inventario.servicio.CategoriaServicio;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CategoriaDTO obtener(@PathParam("id") int id) {
        return categoriaServicio.obtener(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crear(CategoriaDTO categoriaDTO) {
        return Response.status(Response.Status.CREATED)
                .entity(categoriaServicio.crear(categoriaDTO))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizar(@PathParam("id") int id, CategoriaDTO categoriaDTO) {
        return Response.ok(categoriaServicio.actualizar(id, categoriaDTO)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response borrar(@PathParam("id") int id) {
        categoriaServicio.borrar(id);
        return Response.noContent().build();
    }
}