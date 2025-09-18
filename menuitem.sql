CREATE DATABASE kioskdb CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE kioskdb;

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

CREATE TABLE customer (
    cust_id INT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    point INT DEFAULT 0
);
-- 메뉴 카테고리
INSERT INTO menu (name) VALUES ('커피'), ('라떼'), ('스무디'), ('티');

-- 서브메뉴 (예시)
INSERT INTO submenu (menu_id, name, price) VALUES
(1, '아메리카노', 3000),
(1, '에스프레소', 3000),
(1, '카페라떼', 4000),
(1, '카푸치노', 4000),
(2, '초코라떼', 4500),
(2, '녹차라떼', 4500),
(2, '딸기라떼', 5000),
(3, '딸기스무디', 5500),
(3, '망고스무디', 5500),
(4, '캐모마일', 3000),
(4, '페퍼민트', 3000);

-- 토핑 (예시, submenu_id는 위에서 생성된 값에 맞게 입력)
INSERT INTO topping (submenu_id, name, price) VALUES
(1, 'ice', 0),
(1, 'hot', 0),
(1, '헤이즐넛', 500),
(1, '바닐라', 500),
(2, 'ice', 0),
(2, 'hot', 0),
(2, '헤이즐넛', 500),
(2, '바닐라', 500),
(3, 'ice', 0),
(3, 'hot', 0),
(3, '헤이즐넛', 500),
(3, '바닐라', 500),
(4, 'ice', 0),
(4, 'hot', 0),
(4, '헤이즐넛', 500),
(4, '바닐라', 500),
(5, 'ice', 0),
(5, 'hot', 0),
(5, '우유', 0),
(5, '두유', 500),
(6, 'ice', 0),
(6, 'hot', 0),
(6, '우유', 0),
(6, '두유', 500),
(7, 'ice', 0),
(7, 'hot', 0),
(7, '우유', 0),
(7, '두유', 500),
(10, 'ice', 0),
(10, 'hot', 0),
(11, 'ice', 0),
(11, 'hot', 0);