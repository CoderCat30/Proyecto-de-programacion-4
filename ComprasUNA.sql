CREATE DATABASE IF NOT EXISTS ComprasUNABD
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE ComprasUNABD;

CREATE TABLE user_credentials (
  id INT AUTO_INCREMENT,
  email VARCHAR(254) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  CONSTRAINT PK_user_credentials PRIMARY KEY (id),
  CONSTRAINT UQ_user_credentials_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_information (
  user_id INT NOT NULL,
  firstName VARCHAR(55) NOT NULL,
  lastName VARCHAR(55) NOT NULL,
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
  line2 VARCHAR(120),
  city VARCHAR(80) NOT NULL,
  state VARCHAR(80),
  postal_code VARCHAR(20),
  country_code CHAR(2) NOT NULL,
  phone VARCHAR(25),
  is_default_shipping BOOLEAN NOT NULL DEFAULT 0,
  is_default_billing  BOOLEAN NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT PK_user_address PRIMARY KEY (id),
  CONSTRAINT FK_user_address_user
    FOREIGN KEY (user_id) REFERENCES user_credentials(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT CK_country_code CHECK (CHAR_LENGTH(country_code) = 2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE billing_method (
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL,
  provider VARCHAR(40) NOT NULL,
  provider_payment_token VARCHAR(128) NOT NULL,
  brand ENUM('visa','mastercard','amex','discover','diners','jcb','unionpay','other') NOT NULL,
  funding ENUM('credit','debit','prepaid','unknown') NOT NULL DEFAULT 'unknown',
  last4 CHAR(4) NOT NULL,
  exp_month TINYINT NOT NULL,
  exp_year SMALLINT NOT NULL,
  name_on_card VARCHAR(100),
  billing_address_id INT,
  is_default BOOLEAN NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT PK_billing_method PRIMARY KEY (id),
  CONSTRAINT UQ_billing_token UNIQUE (provider_payment_token),
  CONSTRAINT FK_billing_user FOREIGN KEY (user_id)
    REFERENCES user_credentials(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT FK_billing_address FOREIGN KEY (billing_address_id)
    REFERENCES user_address(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT CK_exp_month CHECK (exp_month BETWEEN 1 AND 12),
  CONSTRAINT CK_exp_year CHECK (exp_year BETWEEN 2000 AND 2100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IX_address_user ON user_address(user_id);
CREATE INDEX IX_address_label ON user_address(user_id, label);
CREATE INDEX IX_billing_user ON billing_method(user_id);


DELIMITER $$

CREATE PROCEDURE sp_auth_login(IN p_email VARCHAR(254))
BEGIN
  SELECT id, email, password_hash FROM user_credentials WHERE email = p_email LIMIT 1;
END $$

CREATE PROCEDURE sp_get_user_id_by_email(IN p_email VARCHAR(254))
BEGIN
  SELECT id FROM user_credentials WHERE email = p_email LIMIT 1;
END $$

CREATE PROCEDURE sp_user_profile(IN p_user_id INT)
BEGIN
  SELECT id, email FROM user_credentials WHERE id = p_user_id LIMIT 1;
  SELECT user_id, firstName, lastName, fullName, cedula FROM user_information WHERE user_id = p_user_id LIMIT 1;
END $$

CREATE PROCEDURE sp_user_overview_by_email(IN p_email VARCHAR(254))
BEGIN
  DECLARE v_user_id INT;
  SELECT id INTO v_user_id FROM user_credentials WHERE email = p_email LIMIT 1;
  SELECT id, email FROM user_credentials WHERE id = v_user_id;
  SELECT user_id, firstName, lastName, fullName, cedula FROM user_information WHERE user_id = v_user_id;
  SELECT id, user_id, label, line1, line2, city, state, postal_code, country_code, phone, is_default_shipping, is_default_billing FROM user_address WHERE user_id = v_user_id ORDER BY id DESC;
  SELECT id, user_id, provider, provider_payment_token, brand, funding, last4, exp_month, exp_year, name_on_card, billing_address_id, is_default FROM billing_method WHERE user_id = v_user_id ORDER BY id DESC;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_list_addresses(IN p_user_id INT)
BEGIN
  SELECT id, user_id, label, line1, line2, city, state, postal_code, country_code, phone, is_default_shipping, is_default_billing
  FROM user_address
  WHERE user_id = p_user_id
  ORDER BY is_default_shipping DESC, is_default_billing DESC, id DESC;
END $$

CREATE PROCEDURE sp_add_address(
  IN p_user_id INT,
  IN p_label VARCHAR(20),
  IN p_line1 VARCHAR(120),
  IN p_line2 VARCHAR(120),
  IN p_city VARCHAR(80),
  IN p_state VARCHAR(80),
  IN p_postal_code VARCHAR(20),
  IN p_country_code CHAR(2),
  IN p_phone VARCHAR(25),
  IN p_is_default_shipping BOOLEAN,
  IN p_is_default_billing BOOLEAN
)
BEGIN
  START TRANSACTION;
  IF p_is_default_shipping = 1 THEN
    UPDATE user_address SET is_default_shipping = 0 WHERE user_id = p_user_id;
  END IF;
  IF p_is_default_billing = 1 THEN
    UPDATE user_address SET is_default_billing = 0 WHERE user_id = p_user_id;
  END IF;
  INSERT INTO user_address(user_id,label,line1,line2,city,state,postal_code,country_code,phone,is_default_shipping,is_default_billing)
  VALUES(p_user_id,p_label,p_line1,p_line2,p_city,p_state,p_postal_code,p_country_code,p_phone,p_is_default_shipping,p_is_default_billing);
  SELECT LAST_INSERT_ID() AS address_id;
  COMMIT;
END $$

CREATE PROCEDURE sp_set_default_shipping(IN p_user_id INT, IN p_address_id INT)
BEGIN
  START TRANSACTION;
  UPDATE user_address SET is_default_shipping = 0 WHERE user_id = p_user_id;
  UPDATE user_address SET is_default_shipping = 1 WHERE id = p_address_id AND user_id = p_user_id;
  COMMIT;
END $$

CREATE PROCEDURE sp_set_default_billing_address(IN p_user_id INT, IN p_address_id INT)
BEGIN
  START TRANSACTION;
  UPDATE user_address SET is_default_billing = 0 WHERE user_id = p_user_id;
  UPDATE user_address SET is_default_billing = 1 WHERE id = p_address_id AND user_id = p_user_id;
  COMMIT;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_list_billing_methods(IN p_user_id INT)
BEGIN
  SELECT id, user_id, provider, provider_payment_token, brand, funding, last4, exp_month, exp_year, name_on_card, billing_address_id, is_default
  FROM billing_method
  WHERE user_id = p_user_id
  ORDER BY is_default DESC, id DESC;
END $$

CREATE PROCEDURE sp_add_billing_method(
  IN p_user_id INT,
  IN p_provider VARCHAR(40),
  IN p_provider_payment_token VARCHAR(128),
  IN p_brand VARCHAR(32),
  IN p_funding VARCHAR(16),
  IN p_last4 CHAR(4),
  IN p_exp_month TINYINT,
  IN p_exp_year SMALLINT,
  IN p_name_on_card VARCHAR(100),
  IN p_billing_address_id INT,
  IN p_set_default BOOLEAN
)
BEGIN
  START TRANSACTION;
  IF p_set_default = 1 THEN
    UPDATE billing_method SET is_default = 0 WHERE user_id = p_user_id;
  END IF;
  INSERT INTO billing_method(user_id,provider,provider_payment_token,brand,funding,last4,exp_month,exp_year,name_on_card,billing_address_id,is_default)
  VALUES(p_user_id,p_provider,p_provider_payment_token,p_brand,p_funding,p_last4,p_exp_month,p_exp_year,p_name_on_card,p_billing_address_id,p_set_default);
  SELECT LAST_INSERT_ID() AS billing_method_id;
  COMMIT;
END $$

CREATE PROCEDURE sp_set_default_billing_method(IN p_user_id INT, IN p_billing_method_id INT)
BEGIN
  START TRANSACTION;
  UPDATE billing_method SET is_default = 0 WHERE user_id = p_user_id;
  UPDATE billing_method SET is_default = 1 WHERE id = p_billing_method_id AND user_id = p_user_id;
  COMMIT;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_upsert_user_information(
  IN p_user_id INT,
  IN p_firstName VARCHAR(55),
  IN p_lastName VARCHAR(55),
  IN p_fullName VARCHAR(120),
  IN p_cedula VARCHAR(20)
)
BEGIN
  INSERT INTO user_information(user_id, firstName, lastName, fullName, cedula)
  VALUES(p_user_id, p_firstName, p_lastName, p_fullName, p_cedula)
  ON DUPLICATE KEY UPDATE
    firstName = VALUES(firstName),
    lastName  = VALUES(lastName),
    fullName  = VALUES(fullName),
    cedula    = VALUES(cedula);
  SELECT user_id, firstName, lastName, fullName, cedula FROM user_information WHERE user_id = p_user_id LIMIT 1;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_checkout_snapshot(IN p_user_id INT)
BEGIN
  SELECT id, user_id, label, line1, line2, city, state, postal_code, country_code, phone
  FROM user_address
  WHERE user_id = p_user_id AND is_default_shipping = 1
  ORDER BY id DESC
  LIMIT 1;

  SELECT id, user_id, label, line1, line2, city, state, postal_code, country_code, phone
  FROM user_address
  WHERE user_id = p_user_id AND is_default_billing = 1
  ORDER BY id DESC
  LIMIT 1;

  SELECT id, user_id, provider, brand, funding, last4, exp_month, exp_year, name_on_card, billing_address_id
  FROM billing_method
  WHERE user_id = p_user_id AND is_default = 1
  ORDER BY id DESC
  LIMIT 1;
END $$


-- Tabla de Categorías
CREATE TABLE categories (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description TEXT,
                            image_url VARCHAR(255),
                            is_active BOOLEAN DEFAULT TRUE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de Productos (con campos suficientes para superar los 50 requeridos)
CREATE TABLE products (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          category_id INT NOT NULL,
                          name VARCHAR(200) NOT NULL,
                          description TEXT,
                          sku VARCHAR(50) UNIQUE NOT NULL,
                          price DECIMAL(10,2) NOT NULL,
                          sale_price DECIMAL(10,2),
                          stock_quantity INT DEFAULT 0,
                          weight DECIMAL(8,2),
                          dimensions VARCHAR(100),
                          color VARCHAR(50),
                          size VARCHAR(50),
                          brand VARCHAR(100),
                          model VARCHAR(100),
                          material VARCHAR(100),
                          warranty_months INT DEFAULT 0,
                          origin_country VARCHAR(50),
                          image_url VARCHAR(255),
                          is_active BOOLEAN DEFAULT TRUE,
                          is_featured BOOLEAN DEFAULT FALSE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (category_id) REFERENCES categories(id),
                          INDEX idx_product_category (category_id),
                          INDEX idx_product_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de Carrito de Compras
CREATE TABLE shopping_cart (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT NOT NULL,
                               product_id INT NOT NULL,
                               quantity INT NOT NULL DEFAULT 1,
                               unit_price DECIMAL(10,2) NOT NULL,
                               added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES user_credentials(id) ON DELETE CASCADE,
                               FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                               UNIQUE KEY uk_cart_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla principal de Órdenes (con fechas del flujo de compra)
CREATE TABLE orders (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        order_number VARCHAR(50) UNIQUE NOT NULL,

    -- Estados del flujo
                        order_status ENUM('pending','confirmed','processing','shipped','delivered','cancelled') DEFAULT 'pending',
                        payment_status ENUM('pending','paid','failed') DEFAULT 'pending',

    -- Montos
                        subtotal DECIMAL(10,2) NOT NULL,
                        tax_amount DECIMAL(10,2) DEFAULT 0,
                        shipping_cost DECIMAL(10,2) DEFAULT 0,
                        discount_amount DECIMAL(10,2) DEFAULT 0,
                        total_amount DECIMAL(10,2) NOT NULL,

    -- Información de envío (snapshot simple)
                        shipping_name VARCHAR(120),
                        shipping_address VARCHAR(300),
                        shipping_city VARCHAR(80),
                        shipping_state VARCHAR(80),
                        shipping_postal_code VARCHAR(20),
                        shipping_phone VARCHAR(25),

    -- Información de facturación (snapshot simple)
                        billing_name VARCHAR(120),
                        billing_address VARCHAR(300),
                        billing_city VARCHAR(80),
                        billing_state VARCHAR(80),
                        billing_postal_code VARCHAR(20),

    -- Información de pago
                        payment_method VARCHAR(40),
                        payment_reference VARCHAR(100),

    -- Información de envío
                        shipping_method VARCHAR(100),
                        tracking_number VARCHAR(100),
                        carrier VARCHAR(50),

    -- FECHAS DEL FLUJO DE COMPRA
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        confirmed_at TIMESTAMP NULL,
                        payment_date TIMESTAMP NULL,
                        processing_date TIMESTAMP NULL,
                        shipped_date TIMESTAMP NULL,
                        estimated_delivery_date DATE NULL,
                        delivered_date TIMESTAMP NULL,
                        cancelled_date TIMESTAMP NULL,

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de Items de la Orden
CREATE TABLE order_items (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             order_id INT NOT NULL,
                             product_id INT NOT NULL,
                             product_name VARCHAR(200) NOT NULL,
                             product_sku VARCHAR(50),
                             quantity INT NOT NULL,
                             unit_price DECIMAL(10,2) NOT NULL,
                             line_total DECIMAL(10,2) NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de Historial de Estados
CREATE TABLE order_status_history (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      order_id INT NOT NULL,
                                      previous_status VARCHAR(50),
                                      new_status VARCHAR(50) NOT NULL,
                                      status_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      notes TEXT,
                                      FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
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
