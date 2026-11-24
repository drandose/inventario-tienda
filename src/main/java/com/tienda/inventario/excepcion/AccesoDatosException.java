package com.tienda.inventario.excepcion;

public class AccesoDatosException extends RuntimeException {
    public AccesoDatosException(String message, Throwable cause) {
        super(message, cause);
    }
}