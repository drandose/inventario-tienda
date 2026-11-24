package com.tienda.inventario.dao;

import com.tienda.inventario.excepcion.AccesoDatosException;
import com.tienda.inventario.excepcion.ConflictoException;
import com.tienda.inventario.modelo.Producto;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProductoDao {
    private static final String SELECT_CON_CATEGORIA = "SELECT p.id, p.codigo, p.nombre, p.precio, p.stock, "
            + "p.categoria_id, c.nombre AS categoria_nombre FROM producto p "
            + "JOIN categoria c ON c.id = p.categoria_id ";

    @Resource(lookup = "java:app/jdbc/inventario")
    private DataSource dataSource;

    public ProductoDao() {
    }

    public ProductoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Producto> listar() {
        String sql = SELECT_CON_CATEGORIA + "ORDER BY p.id";
        List<Producto> productos = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                productos.add(leerProducto(resultSet));
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudieron listar los productos.", e);
        }

        return productos;
    }

    public Producto buscarPorId(int id) {
        String sql = SELECT_CON_CATEGORIA + "WHERE p.id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return leerProducto(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo buscar el producto.", e);
        }

        return null;
    }

    public int insertar(Producto producto) {
        String sql = "INSERT INTO producto (codigo, nombre, precio, stock, categoria_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(statement, producto);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            throw new AccesoDatosException("No se pudo obtener el identificador del producto creado.",
                    new SQLException("MySQL no devolvió el identificador generado."));
        } catch (SQLIntegrityConstraintViolationException e) {
            throw traducirIntegridad(e, "No se pudo crear el producto.");
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo crear el producto.", e);
        }
    }

    public void actualizar(Producto producto) {
        String sql = "UPDATE producto SET codigo = ?, nombre = ?, precio = ?, stock = ?, categoria_id = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            asignarParametros(statement, producto);
            statement.setInt(6, producto.getId());
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw traducirIntegridad(e, "No se pudo actualizar el producto.");
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo actualizar el producto.", e);
        }
    }

    public void borrar(int id) {
        String sql = "DELETE FROM producto WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo borrar el producto.", e);
        }
    }

    private Producto leerProducto(ResultSet resultSet) throws SQLException {
        return new Producto(
                resultSet.getInt("id"),
                resultSet.getString("codigo"),
                resultSet.getString("nombre"),
                resultSet.getBigDecimal("precio"),
                resultSet.getInt("stock"),
                resultSet.getInt("categoria_id"),
                resultSet.getString("categoria_nombre")
        );
    }

    private void asignarParametros(PreparedStatement statement, Producto producto) throws SQLException {
        statement.setString(1, producto.getCodigo());
        statement.setString(2, producto.getNombre());
        statement.setBigDecimal(3, producto.getPrecio());
        statement.setInt(4, producto.getStock());
        statement.setInt(5, producto.getCategoriaId());
    }

    private RuntimeException traducirIntegridad(SQLIntegrityConstraintViolationException exception, String mensaje) {
        if (exception.getErrorCode() == 1062 || "23505".equals(exception.getSQLState())) {
            return new ConflictoException("Ya existe un producto con ese código.");
        }
        return new AccesoDatosException(mensaje, exception);
    }
}