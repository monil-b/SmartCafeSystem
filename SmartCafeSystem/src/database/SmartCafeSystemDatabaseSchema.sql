-- Drop database if exists and create fresh
DROP DATABASE IF EXISTS smartcafe;
CREATE DATABASE smartcafe;
USE smartcafe;

-- Table 1: Users (Customers)
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Table 2: Owners
CREATE TABLE owners (
    owner_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 3: Menu Items
CREATE TABLE menu_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    image_path VARCHAR(255),
    description TEXT,
    preparation_time INT DEFAULT 300,
    available BOOLEAN DEFAULT TRUE
);

-- Table 4: Orders
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    ready_time TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Table 5: Order Details
CREATE TABLE order_details (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id)
);

-- Table 6: Kitchen Stations (for parallel processing)
CREATE TABLE kitchen_stations (
    station_id INT AUTO_INCREMENT PRIMARY KEY,
    station_name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    current_order_id INT NULL,
    FOREIGN KEY (current_order_id) REFERENCES orders(order_id)
);

-- Table 7: Order Queue (for priority scheduling)
CREATE TABLE order_queue (
    queue_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    preparation_time INT NOT NULL,
    priority INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'QUEUED',
    assigned_station INT,
    queue_position INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

-- Table 8: Daily Analytics
CREATE TABLE daily_analytics (
    analytics_id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    total_orders INT DEFAULT 0,
    total_revenue DECIMAL(10, 2) DEFAULT 0,
    popular_item_id INT,
    avg_preparation_time INT,
    FOREIGN KEY (popular_item_id) REFERENCES menu_items(item_id)
);

-- Insert default owner (username: admin, password: admin123)
INSERT INTO owners (username, password, full_name, email, phone) 
VALUES ('customer', 'customer123', 'customer@test.com', '9876543211');
-- Insert 3 kitchen stations
INSERT INTO kitchen_stations (station_name) VALUES 
('Station 1'), 
('Station 2'), 
('Station 3');

-- Insert sample menu items
INSERT INTO menu_items (name, price, category, image_path, description, preparation_time) VALUES
-- Beverages
('Cappuccino', 120.00, 'Beverages', 'cappuccino.jpg', 'Classic Italian coffee with steamed milk foam', 180),
('Espresso', 80.00, 'Beverages', 'espresso.jpg', 'Strong black coffee shot', 120),
('Latte', 130.00, 'Beverages', 'latte.jpg', 'Smooth coffee with steamed milk', 180),
('Cold Coffee', 140.00, 'Beverages', 'coldcoffee.jpg', 'Refreshing iced coffee blend', 200),
('Green Tea', 60.00, 'Beverages', 'greentea.jpg', 'Healthy herbal green tea', 150),
('Chocolate Shake', 150.00, 'Beverages', 'shake.jpg', 'Creamy chocolate milkshake', 200),
('Americano', 100.00, 'Beverages', 'americano.jpg', 'Espresso with hot water', 150),
('Mocha', 160.00, 'Beverages', 'mocha.jpg', 'Chocolate flavored coffee', 200),

-- Food
('Veg Sandwich', 100.00, 'Food', 'sandwich.jpg', 'Fresh vegetable sandwich', 300),
('Pasta Alfredo', 180.00, 'Food', 'pasta.jpg', 'Italian style creamy pasta', 420),
('Margherita Pizza', 250.00, 'Food', 'pizza.jpg', 'Classic cheese pizza', 480),
('Veg Burger', 140.00, 'Food', 'burger.jpg', 'Veg patty burger with fries', 360),
('French Fries', 80.00, 'Food', 'fries.jpg', 'Crispy golden french fries', 240),
('Grilled Sandwich', 120.00, 'Food', 'grilledsandwich.jpg', 'Toasted grilled sandwich', 300),
('Paneer Pizza', 280.00, 'Food', 'paneerpizza.jpg', 'Pizza with paneer topping', 480),
('Pasta Arrabiata', 190.00, 'Food', 'pastaarrabiata.jpg', 'Spicy tomato pasta', 420),

-- Desserts
('Chocolate Brownie', 90.00, 'Desserts', 'brownie.jpg', 'Rich chocolate brownie with nuts', 180),
('Vanilla Ice Cream', 70.00, 'Desserts', 'icecream.jpg', 'Premium vanilla ice cream', 120),
('Belgian Waffle', 110.00, 'Desserts', 'waffle.jpg', 'Crispy waffle with maple syrup', 240),
('Cheesecake', 130.00, 'Desserts', 'cheesecake.jpg', 'New York style cheesecake', 180),
('Chocolate Cake', 100.00, 'Desserts', 'cake.jpg', 'Moist chocolate cake slice', 180),
('Tiramisu', 150.00, 'Desserts', 'tiramisu.jpg', 'Italian coffee dessert', 200);

-- Insert sample customer (username: customer, password: customer123)
INSERT INTO users (username, password, email, phone) 
VALUES ('customer', 'customer123', 'customer@test.com', '9876543211');

-- VERIFICATION QUERIES
SELECT '✓ Database created successfully!' as Status;
SELECT COUNT(*) as 'Total Menu Items' FROM menu_items;
SELECT COUNT(*) as 'Total Users' FROM users;
SELECT COUNT(*) as 'Total Owners' FROM owners;
SELECT COUNT(*) as 'Kitchen Stations' FROM kitchen_stations;

-- Show sample data
SELECT '=== MENU ITEMS ===' as '';
SELECT item_id, name, category, price, available FROM menu_items LIMIT 5;

SELECT '=== DEFAULT CREDENTIALS ===' as '';
SELECT 'Owner Login:' as Type, 'admin / admin123' as Credentials
UNION ALL
SELECT 'Customer Login:' as Type, 'customer / customer123' as Credentials;
