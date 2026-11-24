package com.tienda.inventario.dao;

import com.tienda.inventario.excepcion.AccesoDatosException;
import com.tienda.inventario.modelo.Proveedor;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProductoProveedorDao {
    @Resource(lookup = "java:app/jdbc/inventario")
    private DataSource dataSource;

    public List<Proveedor> listarProveedoresDeProducto(int productoId) {
        String sql = "SELECT p.id, p.nombre, p.telefono, p.email FROM proveedor p "
                + "JOIN producto_proveedor pp ON pp.proveedor_id = p.id "
                + "WHERE pp.producto_id = ? ORDER BY p.id";
        List<Proveedor> proveedores = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productoId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    proveedores.add(new Proveedor(
                            resultSet.getInt("id"),
                            resultSet.getString("nombre"),
                            resultSet.getString("telefono"),
                            resultSet.getString("email")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudieron listar los proveedores del producto.", e);
        }

        return proveedores;
    }

    public void borrarPorProducto(int productoId) {
        String sql = "DELETE FROM producto_proveedor WHERE producto_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productoId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudieron borrar los proveedores del producto.", e);
        }
    }

    public void insertar(int productoId, int proveedorId) {
        String sql = "INSERT INTO producto_proveedor (producto_id, proveedor_id) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productoId);
            statement.setInt(2, proveedorId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo asociar el proveedor al producto.", e);
        }
    }
}