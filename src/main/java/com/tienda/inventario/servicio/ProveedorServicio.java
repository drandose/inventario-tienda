package com.tienda.inventario.servicio;

import com.tienda.inventario.dao.ProveedorDao;
import com.tienda.inventario.dto.ProveedorDTO;
import com.tienda.inventario.excepcion.NoEncontradoException;
import com.tienda.inventario.excepcion.ValidacionException;
import com.tienda.inventario.modelo.Proveedor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProveedorServicio {
    @Inject
    private ProveedorDao proveedorDao;

    public List<ProveedorDTO> listar() {
        List<ProveedorDTO> proveedores = new ArrayList<>();
        for (Proveedor proveedor : proveedorDao.listar()) {
            proveedores.add(convertir(proveedor));
        }
        return proveedores;
    }

    public ProveedorDTO obtener(int id) {
        return convertir(obtenerProveedor(id));
    }

    public ProveedorDTO crear(ProveedorDTO proveedorDTO) {
        Proveedor proveedor = validarProveedor(proveedorDTO);
        int id = proveedorDao.insertar(proveedor);
        proveedor.setId(id);
        return convertir(proveedor);
    }

    public ProveedorDTO actualizar(int id, ProveedorDTO proveedorDTO) {
        Proveedor proveedor = validarProveedor(proveedorDTO);
        obtenerProveedor(id);
        proveedor.setId(id);
        proveedorDao.actualizar(proveedor);
        return convertir(proveedor);
    }

    public void borrar(int id) {
        obtenerProveedor(id);
        proveedorDao.borrar(id);
    }

    private Proveedor obtenerProveedor(int id) {
        Proveedor proveedor = proveedorDao.buscarPorId(id);
        if (proveedor == null) {
            throw new NoEncontradoException("No se encontró el proveedor con id " + id + ".");
        }
        return proveedor;
    }

    private Proveedor validarProveedor(ProveedorDTO proveedorDTO) {
        if (proveedorDTO == null || proveedorDTO.getNombre() == null || proveedorDTO.getNombre().isBlank()) {
            throw new ValidacionException("El nombre del proveedor no puede estar vacío.");
        }
        String email = proveedorDTO.getEmail();
        if (email != null && !email.isBlank() && !email.contains("@")) {
            throw new ValidacionException("El email del proveedor debe contener una @.");
        }
        return new Proveedor(0, proveedorDTO.getNombre(), proveedorDTO.getTelefono(), email);
    }

    private ProveedorDTO convertir(Proveedor proveedor) {
        return new ProveedorDTO(proveedor.getId(), proveedor.getNombre(), proveedor.getTelefono(), proveedor.getEmail());
    }
}