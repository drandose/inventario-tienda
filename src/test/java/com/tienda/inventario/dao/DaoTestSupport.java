package com.tienda.inventario.dao;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

abstract class DaoTestSupport {
    protected JdbcDataSource dataSource;

    @BeforeEach
    void cargarEsquema() throws IOException, SQLException {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:" + UUID.randomUUID().toString().replace("-", "")
                + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        try (Connection connection = dataSource.getConnection();
             InputStream script = Objects.requireNonNull(getClass().getResourceAsStream("/esquema-test.sql"))) {
            RunScript.execute(connection, new InputStreamReader(script, StandardCharsets.UTF_8));
        }
    }
}