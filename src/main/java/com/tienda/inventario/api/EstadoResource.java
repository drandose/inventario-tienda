package com.tienda.inventario.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

@Path("/estado")
public class EstadoResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> obtenerEstado() {
        return Map.of("estado", "ok");
    }
}