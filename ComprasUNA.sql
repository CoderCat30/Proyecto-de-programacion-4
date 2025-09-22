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
  fullName VARCHAR(120) NOT NULL,
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
  label ENUM('home','work','shipping','billing','other') DEFAULT 'other',
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
  brand ENUM('visa','mastercard','amex','discover','diners','jcb','unionpay') NOT NULL,
  exp_month TINYINT NOT NULL,
  exp_year SMALLINT NOT NULL,
  name_on_card VARCHAR(100),
  CONSTRAINT PK_billing_method PRIMARY KEY (id),
  CONSTRAINT UQ_billing_token UNIQUE (provider_payment_token),
  CONSTRAINT FK_billing_user FOREIGN KEY (user_id)
    REFERENCES user_credentials(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT CK_exp_month CHECK (exp_month BETWEEN 1 AND 12),
  CONSTRAINT CK_exp_year CHECK (exp_year BETWEEN 2000 AND 2100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IX_address_user ON user_address(user_id);
CREATE INDEX IX_address_label ON user_address(user_id, label);
CREATE INDEX IX_billing_user ON billing_method(user_id);

-- 23
-- Tabla de Categorías
CREATE TABLE categories (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description TEXT,
                            image_url VARCHAR(255),
                            is_active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 32
-- Tabla de Productos (con campos suficientes para superar los 50 requeridos)
CREATE TABLE products (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          category_id INT NOT NULL,
                          name VARCHAR(200) NOT NULL,
                          description TEXT,
                          price DECIMAL(10,2) NOT NULL,
                          stock_quantity INT DEFAULT 0,
                          origin_country VARCHAR(50),
                          image_url VARCHAR(255),
                          FOREIGN KEY (category_id) REFERENCES categories(id),
                          INDEX idx_product_category (category_id),
                          INDEX idx_product_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 35
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

-- 40
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


-- Datos de ejemplo
INSERT INTO categories (name, description, is_active) VALUES
                                                          ('Electrónicos', 'Dispositivos electrónicos y tecnología', TRUE),
                                                          ('Ropa', 'Vestimenta y accesorios', TRUE),
                                                          ('Hogar', 'Artículos para el hogar', TRUE),
                                                          ('Deportes', 'Equipos deportivos', TRUE),
                                                          ('Libros', 'Libros y material educativo', TRUE);

INSERT INTO products (category_id, name, description, sku, price, stock_quantity, brand, color, weight, is_active) VALUES
                                                                                                                       (1, 'Smartphone Samsung Galaxy', 'Teléfono inteligente último modelo', 'SAMS-001', 450000.00, 50, 'Samsung', 'Negro', 0.18, TRUE),
                                                                                                                       (1, 'Laptop HP Pavilion', 'Laptop para uso general', 'HP-LAP-001', 680000.00, 25, 'HP', 'Plateado', 2.1, TRUE),
                                                                                                                       (2, 'Camiseta Nike', 'Camiseta deportiva', 'NIKE-CAM-001', 25000.00, 100, 'Nike', 'Azul', 0.2, TRUE),
                                                                                                                       (3, 'Cafetera Automática', 'Cafetera programable', 'CAF-001', 85000.00, 30, 'Black & Decker', 'Negro', 2.5, TRUE),
                                                                                                                       (4, 'Balón de Fútbol', 'Balón profesional', 'BAL-001', 35000.00, 75, 'Adidas', 'Blanco', 0.4, TRUE),
                                                                                                                       (5, 'Libro de Programación Java', 'Guía completa de Java', 'LIB-JAVA-001', 45000.00, 40, 'O Reilly', 'Multicolor', 0.8, TRUE);
