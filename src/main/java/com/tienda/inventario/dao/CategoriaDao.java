package com.tienda.inventario.dao;

import com.tienda.inventario.excepcion.AccesoDatosException;
import com.tienda.inventario.modelo.Categoria;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.Resource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CategoriaDao {
    @Resource(lookup = "java:app/jdbc/inventario")
    private DataSource dataSource;

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
}