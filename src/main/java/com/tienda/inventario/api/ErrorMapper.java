package com.tienda.inventario.api;

import com.tienda.inventario.excepcion.ConflictoException;
import com.tienda.inventario.excepcion.NoEncontradoException;
import com.tienda.inventario.excepcion.ValidacionException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Provider
public class ErrorMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException exception) {
        if (exception instanceof WebApplicationException webApplicationException) {
            return webApplicationException.getResponse();
        }

        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        if (exception instanceof ValidacionException) {
            status = Response.Status.BAD_REQUEST;
        } else if (exception instanceof NoEncontradoException) {
            status = Response.Status.NOT_FOUND;
        } else if (exception instanceof ConflictoException) {
            status = Response.Status.CONFLICT;
        }

        String mensaje = exception.getMessage();
        if (mensaje == null || mensaje.isBlank()) {
            mensaje = "Error interno del servidor.";
        }

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", mensaje))
                .build();
    }
}