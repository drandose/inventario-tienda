SET NAMES utf8mb4;

CREATE TABLE categoria (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_categoria_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4;

CREATE TABLE proveedor (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(30),
    email VARCHAR(254),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4;

CREATE TABLE producto (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL,
    categoria_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_producto_codigo (codigo),
    CONSTRAINT chk_producto_precio CHECK (precio > 0),
    CONSTRAINT chk_producto_stock CHECK (stock >= 0),
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4;

CREATE TABLE producto_proveedor (
    producto_id INT UNSIGNED NOT NULL,
    proveedor_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (producto_id, proveedor_id),
    CONSTRAINT fk_producto_proveedor_producto
        FOREIGN KEY (producto_id) REFERENCES producto (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_producto_proveedor_proveedor
        FOREIGN KEY (proveedor_id) REFERENCES proveedor (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4;

INSERT INTO categoria (id, nombre) VALUES
    (1, 'Informática'),
    (2, 'Periféricos'),
    (3, 'Accesorios');

INSERT INTO proveedor (id, nombre, telefono, email) VALUES
    (1, 'Suministros del Norte', '555-0101', 'ventas@norte.example'),
    (2, 'Distribuciones Central', '555-0102', 'pedidos@central.example'),
    (3, 'Tecnología Sur', '555-0103', 'contacto@sur.example');

INSERT INTO producto (id, codigo, nombre, precio, stock, categoria_id) VALUES
    (1, 'INF-001', 'Portátil 14 pulgadas', 749.99, 12, 1),
    (2, 'INF-002', 'Monitor 27 pulgadas', 229.50, 8, 1),
    (3, 'PER-001', 'Teclado mecánico', 79.90, 25, 2),
    (4, 'PER-002', 'Ratón inalámbrico', 34.95, 40, 2),
    (5, 'ACC-001', 'Cable USB-C', 12.50, 60, 3),
    (6, 'ACC-002', 'Adaptador HDMI', 18.75, 30, 3);

INSERT INTO producto_proveedor (producto_id, proveedor_id) VALUES
    (1, 1),
    (1, 2),
    (2, 1),
    (2, 3),
    (3, 2),
    (3, 3),
    (4, 1),
    (4, 2),
    (5, 2),
    (5, 3),
    (6, 1),
    (6, 3);