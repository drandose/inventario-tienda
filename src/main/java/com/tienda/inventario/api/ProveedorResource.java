package com.tienda.inventario.api;

import com.tienda.inventario.dto.ProveedorDTO;
import com.tienda.inventario.servicio.ProveedorServicio;
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
@Path("/proveedores")
public class ProveedorResource {
    @Inject
    private ProveedorServicio proveedorServicio;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProveedorDTO> listar() {
        return proveedorServicio.listar();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProveedorDTO obtener(@PathParam("id") int id) {
        return proveedorServicio.obtener(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crear(ProveedorDTO proveedorDTO) {
        return Response.status(Response.Status.CREATED)
                .entity(proveedorServicio.crear(proveedorDTO))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizar(@PathParam("id") int id, ProveedorDTO proveedorDTO) {
        return Response.ok(proveedorServicio.actualizar(id, proveedorDTO)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response borrar(@PathParam("id") int id) {
        proveedorServicio.borrar(id);
        return Response.noContent().build();
    }
}