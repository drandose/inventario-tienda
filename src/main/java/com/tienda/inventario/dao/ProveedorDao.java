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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProveedorDao {
    @Resource(lookup = "java:app/jdbc/inventario")
    private DataSource dataSource;

    public ProveedorDao() {
    }

    public ProveedorDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Proveedor> listar() {
        String sql = "SELECT id, nombre, telefono, email FROM proveedor ORDER BY id";
        List<Proveedor> proveedores = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                proveedores.add(leerProveedor(resultSet));
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudieron listar los proveedores.", e);
        }

        return proveedores;
    }

    public Proveedor buscarPorId(int id) {
        String sql = "SELECT id, nombre, telefono, email FROM proveedor WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return leerProveedor(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo buscar el proveedor.", e);
        }

        return null;
    }

    public int insertar(Proveedor proveedor) {
        String sql = "INSERT INTO proveedor (nombre, telefono, email) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(statement, proveedor);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            throw new AccesoDatosException("No se pudo obtener el identificador del proveedor creado.",
                    new SQLException("MySQL no devolvió el identificador generado."));
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo crear el proveedor.", e);
        }
    }

    public void actualizar(Proveedor proveedor) {
        String sql = "UPDATE proveedor SET nombre = ?, telefono = ?, email = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            asignarParametros(statement, proveedor);
            statement.setInt(4, proveedor.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo actualizar el proveedor.", e);
        }
    }

    public void borrar(int id) {
        String sql = "DELETE FROM proveedor WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo borrar el proveedor.", e);
        }
    }

    private Proveedor leerProveedor(ResultSet resultSet) throws SQLException {
        return new Proveedor(
                resultSet.getInt("id"),
                resultSet.getString("nombre"),
                resultSet.getString("telefono"),
                resultSet.getString("email")
        );
    }

    private void asignarParametros(PreparedStatement statement, Proveedor proveedor) throws SQLException {
        statement.setString(1, proveedor.getNombre());
        statement.setString(2, proveedor.getTelefono());
        statement.setString(3, proveedor.getEmail());
    }
}