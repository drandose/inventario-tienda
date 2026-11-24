package com.tienda.inventario.dao;

import com.tienda.inventario.excepcion.AccesoDatosException;
import com.tienda.inventario.excepcion.ConflictoException;
import com.tienda.inventario.modelo.Categoria;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.Resource;

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
public class CategoriaDao {
    @Resource(lookup = "java:app/jdbc/inventario")
    private DataSource dataSource;

    public CategoriaDao() {
    }

    public CategoriaDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Categoria> listar() {
        String sql = "SELECT id, nombre FROM categoria ORDER BY id";
        List<Categoria> categorias = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                categorias.add(new Categoria(
                        resultSet.getInt("id"),
                        resultSet.getString("nombre")
                ));
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudieron listar las categorías.", e);
        }

        return categorias;
    }

    public Categoria buscarPorId(int id) {
        String sql = "SELECT id, nombre FROM categoria WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Categoria(resultSet.getInt("id"), resultSet.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo buscar la categoría.", e);
        }

        return null;
    }

    public int insertar(Categoria categoria) {
        String sql = "INSERT INTO categoria (nombre) VALUES (?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, categoria.getNombre());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            throw new AccesoDatosException("No se pudo obtener el identificador de la categoría creada.",
                    new SQLException("MySQL no devolvió el identificador generado."));
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new ConflictoException("Ya existe una categoría con ese nombre.");
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo crear la categoría.", e);
        }
    }

    public void actualizar(Categoria categoria) {
        String sql = "UPDATE categoria SET nombre = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoria.getNombre());
            statement.setInt(2, categoria.getId());
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new ConflictoException("Ya existe una categoría con ese nombre.");
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo actualizar la categoría.", e);
        }
    }

    public void borrar(int id) {
        String sql = "DELETE FROM categoria WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new ConflictoException("No se puede borrar la categoría porque tiene productos asociados.");
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo borrar la categoría.", e);
        }
    }
}