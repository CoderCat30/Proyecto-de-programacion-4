DROP DATABASE IF EXISTS ComprasUNABD;

CREATE DATABASE IF NOT EXISTS ComprasUNABD
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE ComprasUNABD;

CREATE TABLE user_credentials (
  id INT AUTO_INCREMENT,
  email VARCHAR(254) NOT NULL,
  role varchar(10) default 'user',
  password_hash VARCHAR(255) NOT NULL,
  CONSTRAINT PK_user_credentials PRIMARY KEY (id),
  CONSTRAINT UQ_user_credentials_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_information (
  user_id INT NOT NULL,
  full_name VARCHAR(120) NOT NULL,
  cedula VARCHAR(20) NOT NULL,
  CONSTRAINT PK_user_information PRIMARY KEY (user_id),
  CONSTRAINT UQ_user_information_cedula UNIQUE (cedula),
  CONSTRAINT FK_user_information_user
    FOREIGN KEY (user_id) REFERENCES user_credentials(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_address (
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL,
  label VARCHAR(255) DEFAULT 'other',
  line1 VARCHAR(120) NOT NULL,
  city VARCHAR(80) NOT NULL,
  state VARCHAR(80),
  postal_code VARCHAR(20),
  phone VARCHAR(25),
  CONSTRAINT PK_user_address PRIMARY KEY (id),
  CONSTRAINT FK_user_address_user
    FOREIGN KEY (user_id) REFERENCES user_credentials(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15

CREATE TABLE billing_method (
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL,
  card_number varchar (12)NOT NULL,
  brand varchar(50),
  exp_month TINYINT NOT NULL,
  exp_year SMALLINT NOT NULL,
  name_on_card VARCHAR(100),
  CONSTRAINT PK_billing_method PRIMARY KEY (id),
  CONSTRAINT FK_billing_user FOREIGN KEY (user_id)
    REFERENCES user_credentials(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT CK_exp_month CHECK (exp_month BETWEEN 1 AND 12),
  CONSTRAINT CK_exp_year CHECK (exp_year BETWEEN 2000 AND 2100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE bank (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bank_name VARCHAR(100) NOT NULL,
    card_number VARCHAR(12) NOT NULL,
    brand varchar(50) NOT NULL,
    exp_month TINYINT NOT NULL,
    exp_year SMALLINT NOT NULL,
    name_on_card VARCHAR(100),
    balance DECIMAL(10,2) DEFAULT 0,       -- saldo de la tarjeta
    CONSTRAINT CK_bank_exp_month CHECK (exp_month BETWEEN 1 AND 12),
    CONSTRAINT CK_bank_exp_year CHECK (exp_year BETWEEN 2000 AND 2100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 30

CREATE INDEX IX_address_user ON user_address(user_id);
CREATE INDEX IX_address_label ON user_address(user_id, label);
CREATE INDEX IX_billing_user ON billing_method(user_id);

-- Tabla de Categorías
CREATE TABLE categories (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description TEXT,
                            image_url VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 34
-- Tabla de Productos (con campos suficientes para superar los 50 requeridos)
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    INDEX idx_product_category (category_id),
    INDEX idx_product_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 42
-- Tabla de Carrito de Compras
CREATE TABLE shopping_cart (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT NOT NULL,
                               product_id INT NOT NULL,
                               quantity INT NOT NULL DEFAULT 1,
                               unit_price DECIMAL(10,2) NOT NULL,
                               FOREIGN KEY (user_id) REFERENCES user_credentials(id) ON DELETE CASCADE,
                               FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                               UNIQUE KEY uk_cart_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 47 ez
-- Tabla principal de Órdenes (agregar fecha y fecha)
CREATE TABLE orders (
    -- agregar fecha
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        user_fullname varchar(120),
                        order_number VARCHAR(50) UNIQUE NOT NULL,
    -- Estados del flujo
    -- simplificar a no enntrega y entregado, con numero o bool, enum = problema
                        order_status ENUM('pending','confirmed','processing','shipped','delivered','cancelled') DEFAULT 'pending',
    -- Monto
                        total DECIMAL(10,2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de Items de la Orden
CREATE TABLE order_items (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             order_id INT NOT NULL,
                             product_id INT NOT NULL,
                             quantity INT NOT NULL,
                             FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- tabla de Configuración para administrar el tiempo de sesión
CREATE TABLE configuracion (
    clave VARCHAR(100) NOT NULL PRIMARY KEY,
    valor_segundos INT NOT NULL
);

INSERT INTO configuracion (clave, valor_segundos)
VALUES ('tiempo_sesion', 10)
    ON DUPLICATE KEY UPDATE valor_segundos = 10;


-- contraseñas
-- admin
-- 1234
-- 2345

INSERT INTO user_credentials (email, role, password_hash) VALUES
('admin@gmail.com', 'admin', '$2a$12$3uZi74cRqPKqauE0s/CrJe6WICwUuRS19mh0/oGSTG0De8CjLl7Q2'),
('user1@gmail.com', 'user', '$2a$12$u8rixxeRVoMHf3swuh4w9.yjMJRL9/o3WPg0NfIfRCcYRfh5IOyhm'),
('user2@gmail.com', 'user', '$2a$12$ewnvHd8q7wBwK4Pkaw6w1eRInDN0Iw22gBSecDTwNuYypFhSSd44y');

INSERT INTO user_information (user_id, full_Name, cedula) VALUES
(1, 'Administrador Principal', '16161'),
(2, 'Usuario Uno', '641561'),
(3, 'Usuario Dos', '146544');


INSERT INTO user_address (user_id, label, line1, city, state, postal_code, phone) VALUES
(1, 'home', 'Calle Principal 123', 'San José', 'San José', '10101', '88880001'),
(2, 'home', 'Avenida Secundaria 45', 'Alajuela', 'Alajuela', '20102', '88880002'),
(3, 'home', 'Boulevard Central 678', 'Heredia', 'Heredia', '30103', '88880003');


INSERT INTO bank (bank_name, card_number, brand, exp_month, exp_year, name_on_card, balance)
VALUES
    ('Banco Nacional', '234567890123', 'mastercard', 7, 2027, 'Usuario Dos', 500.00),
    ('Banco de Costa Rica', '345678901234', 'visa', 5, 2028, 'Usuario Tres', 30000.00);

INSERT INTO billing_method (user_id, card_number, brand, exp_month, exp_year, name_on_card)
VALUES
    (2, '234567890123', 'mastercard', 7, 2027, 'Usuario Dos'),
    (3, '345678901234', 'visa', 5, 2028, 'Usuario Tres');










