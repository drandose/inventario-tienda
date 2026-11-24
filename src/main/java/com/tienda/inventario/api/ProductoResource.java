package com.tienda.inventario.api;

import com.tienda.inventario.dto.ProductoDTO;
import com.tienda.inventario.servicio.ProductoServicio;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RequestScoped
@Path("/productos")
public class ProductoResource {
    @Inject
    private ProductoServicio productoServicio;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProductoDTO> listar() {
        return productoServicio.listar();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProductoDTO obtener(@PathParam("id") int id) {
        return productoServicio.obtener(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crear(ProductoDTO productoDTO) {
        return Response.status(Response.Status.CREATED)
                .entity(productoServicio.crear(productoDTO))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizar(@PathParam("id") int id, ProductoDTO productoDTO) {
        return Response.ok(productoServicio.actualizar(id, productoDTO)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response borrar(@PathParam("id") int id) {
        productoServicio.borrar(id);
        return Response.noContent().build();
    }
}