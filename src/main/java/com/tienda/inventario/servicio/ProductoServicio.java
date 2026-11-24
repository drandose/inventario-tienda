package com.tienda.inventario.servicio;

import com.tienda.inventario.dao.CategoriaDao;
import com.tienda.inventario.dao.ProductoDao;
import com.tienda.inventario.dao.ProductoProveedorDao;
import com.tienda.inventario.dao.ProveedorDao;
import com.tienda.inventario.dto.ProveedorDTO;
import com.tienda.inventario.dto.ProductoDTO;
import com.tienda.inventario.excepcion.NoEncontradoException;
import com.tienda.inventario.excepcion.ValidacionException;
import com.tienda.inventario.modelo.Categoria;
import com.tienda.inventario.modelo.Producto;
import com.tienda.inventario.modelo.Proveedor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ProductoServicio {
    @Inject
    private ProductoDao productoDao;

    @Inject
    private CategoriaDao categoriaDao;

    @Inject
    private ProveedorDao proveedorDao;

    @Inject
    private ProductoProveedorDao productoProveedorDao;

    public List<ProductoDTO> listar() {
        List<ProductoDTO> productos = new ArrayList<>();
        for (Producto producto : productoDao.listar()) {
            productos.add(convertir(producto));
        }
        return productos;
    }

    public ProductoDTO obtener(int id) {
        return convertir(obtenerProducto(id));
    }

    public ProductoDTO crear(ProductoDTO productoDTO) {
        Producto producto = validarProducto(productoDTO);
        int id = productoDao.insertar(producto);
        return convertir(obtenerProducto(id));
    }

    public ProductoDTO actualizar(int id, ProductoDTO productoDTO) {
        Producto producto = validarProducto(productoDTO);
        obtenerProducto(id);
        producto.setId(id);
        productoDao.actualizar(producto);
        return convertir(obtenerProducto(id));
    }

    public void borrar(int id) {
        obtenerProducto(id);
        productoDao.borrar(id);
    }

    public List<ProveedorDTO> listarProveedoresDeProducto(int productoId) {
        obtenerProducto(productoId);
        List<ProveedorDTO> proveedores = new ArrayList<>();
        for (Proveedor proveedor : productoProveedorDao.listarProveedoresDeProducto(productoId)) {
            proveedores.add(new ProveedorDTO(proveedor.getId(), proveedor.getNombre(), proveedor.getTelefono(),
                    proveedor.getEmail()));
        }
        return proveedores;
    }

    @Transactional
    public void asignarProveedores(int productoId, List<Integer> proveedorIds) {
        obtenerProducto(productoId);
        if (proveedorIds == null) {
            throw new ValidacionException("La lista de proveedores no puede ser nula.");
        }

        Set<Integer> idsUnicos = new HashSet<>();
        for (Integer proveedorId : proveedorIds) {
            if (proveedorId == null) {
                throw new ValidacionException("El identificador del proveedor no puede ser nulo.");
            }
            if (!idsUnicos.add(proveedorId)) {
                throw new ValidacionException("La lista no puede contener proveedores repetidos.");
            }
            if (proveedorDao.buscarPorId(proveedorId) == null) {
                throw new NoEncontradoException("No se encontró el proveedor con id " + proveedorId + ".");
            }
        }

        productoProveedorDao.borrarPorProducto(productoId);
        for (Integer proveedorId : proveedorIds) {
            productoProveedorDao.insertar(productoId, proveedorId);
        }
    }

    private Producto obtenerProducto(int id) {
        Producto producto = productoDao.buscarPorId(id);
        if (producto == null) {
            throw new NoEncontradoException("No se encontró el producto con id " + id + ".");
        }
        return producto;
    }

    private Producto validarProducto(ProductoDTO productoDTO) {
        if (productoDTO == null || productoDTO.getCodigo() == null || productoDTO.getCodigo().isBlank()) {
            throw new ValidacionException("El código del producto no puede estar vacío.");
        }
        if (productoDTO.getNombre() == null || productoDTO.getNombre().isBlank()) {
            throw new ValidacionException("El nombre del producto no puede estar vacío.");
        }
        if (productoDTO.getPrecio() == null || productoDTO.getPrecio().signum() <= 0) {
            throw new ValidacionException("El precio del producto debe ser mayor que 0.");
        }
        if (productoDTO.getStock() < 0) {
            throw new ValidacionException("El stock del producto no puede ser negativo.");
        }

        Categoria categoria = categoriaDao.buscarPorId(productoDTO.getCategoriaId());
        if (categoria == null) {
            throw new ValidacionException("La categoría indicada no existe.");
        }

        return new Producto(0, productoDTO.getCodigo(), productoDTO.getNombre(), productoDTO.getPrecio(),
                productoDTO.getStock(), productoDTO.getCategoriaId());
    }

    private ProductoDTO convertir(Producto producto) {
        return new ProductoDTO(producto.getId(), producto.getCodigo(), producto.getNombre(), producto.getPrecio(),
                producto.getStock(), producto.getCategoriaId(), producto.getCategoriaNombre());
    }
}