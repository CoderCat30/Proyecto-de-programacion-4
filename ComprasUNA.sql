DROP DATABASE IF EXISTS ComprasUNABD;

CREATE DATABASE IF NOT EXISTS ComprasUNABD
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE ComprasUNABD;

CREATE TABLE user_credentials (
  id INT AUTO_INCREMENT,
  email VARCHAR(254) NOT NULL,
  role varchar(10) NOT NULL,
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
-- Tabla principal de Órdenes (con fechas del flujo de compra)
CREATE TABLE orders (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        user_fullname varchar(120),
                        order_number VARCHAR(50) UNIQUE NOT NULL,
    -- Estados del flujo
                        order_status ENUM('pending','confirmed','processing','shipped','delivered','cancelled') DEFAULT 'pending',
                        payment_status ENUM('pending','paid','failed') DEFAULT 'pending',
    -- Montos
                        total DECIMAL(10,2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de Items de la Orden
CREATE TABLE order_items (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             order_id INT NOT NULL,
                             product_id INT NOT NULL,
                             quantity INT NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO user_credentials (email, role, password_hash) VALUES
('admin@gmail.com', 'admin', 'admin'),
('user1@gmail.com', 'user', '1234'),
('user2@gmail.com', 'user', 'abcd');

INSERT INTO user_information (user_id, full_Name, cedula) VALUES
(1, 'Administrador Principal', '00000001'),
(2, 'Usuario Uno', '00000002'),
(3, 'Usuario Dos', '00000003');


INSERT INTO user_address (user_id, label, line1, city, state, postal_code, phone) VALUES
(1, 'home', 'Calle Principal 123', 'San José', 'San José', '10101', '88880001'),
(2, 'home', 'Avenida Secundaria 45', 'Alajuela', 'Alajuela', '20102', '88880002'),
(3, 'home', 'Boulevard Central 678', 'Heredia', 'Heredia', '30103', '88880003');


INSERT INTO bank (bank_name, card_number, brand, exp_month, exp_year, name_on_card, balance) VALUES
('Banco Central', '123456789012', 'visa', 12, 2025, 'Administrador Principal', 5000.00),
('Banco Nacional', '234567890123', 'mastercard', 6, 2026, 'Usuario Uno', 1500.50),
('Banco de Costa Rica', '345678901234', 'amex', 11, 2024, 'Usuario Dos', 300.75),
('Scotiabank', '456789012345', 'discover', 9, 2025, 'Cooperativa', 2000.00);

INSERT INTO categories (name, description, image_url) VALUES
('Electrónicos', 'Dispositivos electrónicos y tecnología', 'img/electronicos.jpg'),
('Ropa', 'Vestimenta y accesorios', 'img/ropa.jpg'),
('Hogar', 'Artículos para el hogar', 'img/hogar.jpg'),
('Deportes', 'Equipos y accesorios deportivos', 'img/deportes.jpg'),
('Libros', 'Libros y material educativo', 'img/libros.jpg');

INSERT INTO products (category_id, name, description, price, stock_quantity, image_url, is_active) VALUES
(1, 'Smartphone X1', 'Teléfono inteligente con pantalla de 6.5 pulgadas', 499.99, 50, 'img/product_def.jpg', TRUE),
(1, 'Laptop Pro 15', 'Portátil potente para trabajo y juegos', 1299.99, 30, 'img/product_def.jpg', TRUE),
(1, 'Auriculares Bluetooth', 'Auriculares inalámbricos con cancelación de ruido', 79.99, 100, 'img/product_def.jpg', TRUE),
(1, 'Smartwatch 2', 'Reloj inteligente con monitor de actividad', 149.99, 60, 'img/product_def.jpg', TRUE),
(1, 'Cámara Digital', 'Cámara profesional con lente intercambiable', 899.99, 20, 'img/product_def.jpg', TRUE),
(2, 'Camiseta Básica', 'Camiseta de algodón, varios colores', 19.99, 200, 'img/product_def.jpg', TRUE),
(2, 'Jeans Regular', 'Pantalón de mezclilla cómodo', 39.99, 150, 'img/product_def.jpg', TRUE),
(2, 'Chaqueta Ligera', 'Chaqueta para clima templado', 59.99, 80, 'img/product_def.jpg', TRUE),
(2, 'Zapatillas Deportivas', 'Calzado cómodo para correr', 49.99, 120, 'img/product_def.jpg', TRUE),
(2, 'Gorra Deportiva', 'Gorra ajustable de algodón', 14.99, 100, 'img/product_def.jpg', TRUE),
(3, 'Sofá 3 Plazas', 'Sofá cómodo de tela', 399.99, 15, 'img/product_def.jpg', TRUE),
(3, 'Mesa de Comedor', 'Mesa de madera para 6 personas', 299.99, 10, 'img/product_def.jpg', TRUE),
(3, 'Lámpara de Techo', 'Lámpara moderna para sala', 79.99, 25, 'img/product_def.jpg', TRUE),
(3, 'Alfombra Grande', 'Alfombra decorativa para sala', 59.99, 30, 'img/product_def.jpg', TRUE),
(3, 'Estantería', 'Estantería de madera para libros', 89.99, 20, 'img/product_def.jpg', TRUE),
(4, 'Balón de Fútbol', 'Balón oficial para entrenamiento', 29.99, 100, 'img/product_def.jpg', TRUE),
(4, 'Raqueta de Tenis', 'Raqueta ligera para principiantes', 49.99, 40, 'img/product_def.jpg', TRUE),
(4, 'Guantes de Boxeo', 'Guantes acolchados de entrenamiento', 34.99, 60, 'img/product_def.jpg', TRUE),
(5, 'Libro de Matemáticas', 'Libro educativo para secundaria', 24.99, 70, 'img/product_def.jpg', TRUE),
(5, 'Novela de Aventura', 'Historia emocionante de aventuras', 14.99, 80, 'img/product_def.jpg', TRUE);










