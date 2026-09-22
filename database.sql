CREATE DATABASE IF NOT EXISTS healthfirst_pims;

USE healthfirst_pims;

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Cashier') NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

CREATE TABLE suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT
);

CREATE TABLE medicines (
    medicine_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    company VARCHAR(100),
    medicine_type VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    quantity_in_stock INT NOT NULL,
    reorder_level INT NOT NULL,
    expiry_date DATE NOT NULL,
    supplier_id INT,
    FOREIGN KEY (supplier_id)
        REFERENCES suppliers(supplier_id)
);

CREATE TABLE sales (
    sale_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);

CREATE TABLE sale_items (
    sale_item_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_id INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity_sold INT NOT NULL,
    price_at_sale DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id)
        REFERENCES sales(sale_id),
    FOREIGN KEY (medicine_id)
        REFERENCES medicines(medicine_id)
);

INSERT INTO users
(username, password, role, full_name)
VALUES
('admin', 'admin123', 'Admin', 'System Administrator'),
('cashier', 'cash123', 'Cashier', 'HealthFirst Cashier');

INSERT INTO suppliers
(name, contact_person, phone, email, address)
VALUES
('PharmaCare Distributors', 'Thabo Mokoena', '0115551001',
 'info@pharmacare.co.za', 'Johannesburg, Gauteng'),

('MediSupply SA', 'Lerato Molefe', '0115551002',
 'sales@medisupply.co.za', 'Pretoria, Gauteng'),

('HealthMed Suppliers', 'James Nkosi', '0115551003',
 'orders@healthmed.co.za', 'Soweto, Gauteng');

INSERT INTO medicines
(name, company, medicine_type, price,
 quantity_in_stock, reorder_level, expiry_date, supplier_id)
VALUES
('Panado 500mg', 'Adcock Ingram', 'Tablet',
 35.99, 100, 20, '2027-08-31', 1),

('Myprodol Capsules', 'Adcock Ingram', 'Capsule',
 59.99, 75, 15, '2027-05-31', 1),

('Benylin Syrup', 'Johnson & Johnson', 'Syrup',
 89.99, 40, 10, '2027-03-31', 2),

('Betadine Cream', 'Kenvue', 'Cream',
 72.50, 25, 10, '2026-12-31', 2),

('Voltaren Tablets', 'Novartis', 'Tablet',
 95.00, 8, 10, '2027-01-31', 3);