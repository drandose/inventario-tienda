package com.tienda.inventario.excepcion;

public class ConflictoException extends RuntimeException {
    public ConflictoException(String message) {
        super(message);
    }
}