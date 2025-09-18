-- 1. 데이터베이스 생성
CREATE DATABASE kioskdb CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 2. 사용할 데이터베이스 선택
CREATE DATABASE kioskdb CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE kioskdb;

CREATE TABLE customer (
    cust_id INT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    point INT DEFAULT 0
);
CREATE TABLE menu (
    menu_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);
CREATE TABLE submenu (
    submenu_id INT AUTO_INCREMENT PRIMARY KEY,
    menu_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    price INT NOT NULL,
    FOREIGN KEY (menu_id) REFERENCES menu(menu_id)
);
CREATE TABLE topping (
    topping_id INT AUTO_INCREMENT PRIMARY KEY,
    submenu_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    price INT DEFAULT 0,
    FOREIGN KEY (submenu_id) REFERENCES submenu(submenu_id)
);
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    cust_id INT,
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_price INT NOT NULL,
    payment ENUM('CARD','CASH','APPPAY') NOT NULL,  -- 예: CARD, CASH, APPPAY
    FOREIGN KEY (cust_id) REFERENCES customer(cust_id)
);
CREATE TABLE order_submenu (
    order_submenu_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    submenu_id INT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (submenu_id) REFERENCES submenu(submenu_id)
);
CREATE TABLE order_topping (
    order_submenu_id INT NOT NULL,
    topping_id INT NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (order_submenu_id, topping_id),
    FOREIGN KEY (order_submenu_id) REFERENCES order_submenu(order_submenu_id),
    FOREIGN KEY (topping_id) REFERENCES topping(topping_id)
);