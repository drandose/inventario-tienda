package com.tienda.inventario.servicio;

import com.tienda.inventario.dao.CategoriaDao;
import com.tienda.inventario.dao.ProductoDao;
import com.tienda.inventario.dto.ProductoDTO;
import com.tienda.inventario.excepcion.NoEncontradoException;
import com.tienda.inventario.excepcion.ValidacionException;
import com.tienda.inventario.modelo.Categoria;
import com.tienda.inventario.modelo.Producto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProductoServicio {
    @Inject
    private ProductoDao productoDao;

    @Inject
    private CategoriaDao categoriaDao;

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