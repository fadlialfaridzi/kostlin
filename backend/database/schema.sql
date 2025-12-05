-- Kostlin Database Schema
-- Create database
CREATE DATABASE IF NOT EXISTS kostlin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE kostlin;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OTP table for password reset
CREATE TABLE IF NOT EXISTS otp_codes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email_code (email, code),
    INDEX idx_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Kos properties table
CREATE TABLE IF NOT EXISTS kos_properties (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    price_per_month INT NOT NULL,
    rating_value DECIMAL(3,2) DEFAULT 0.00,
    type ENUM('PUTRA', 'PUTRI', 'CAMPUR') NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    is_popular BOOLEAN DEFAULT FALSE,
    is_recommended BOOLEAN DEFAULT FALSE,
    owner_email VARCHAR(255),
    owner_phone VARCHAR(20),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (type),
    INDEX idx_popular (is_popular),
    INDEX idx_recommended (is_recommended),
    INDEX idx_location (location)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Kos facilities table
CREATE TABLE IF NOT EXISTS kos_facilities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    kos_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    icon VARCHAR(50),
    is_available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (kos_id) REFERENCES kos_properties(id) ON DELETE CASCADE,
    INDEX idx_kos_id (kos_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    kos_id INT NOT NULL,
    user_id INT NOT NULL,
    rating DECIMAL(3,2) NOT NULL CHECK (rating >= 0 AND rating <= 5),
    comment TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (kos_id) REFERENCES kos_properties(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_kos_id (kos_id),
    INDEX idx_user_id (user_id),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Favorites table
CREATE TABLE IF NOT EXISTS favorites (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    kos_id INT NOT NULL,
    status ENUM('ACTIVE', 'REMOVED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (kos_id) REFERENCES kos_properties(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_kos (user_id, kos_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    kos_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    capacity INT DEFAULT 1,
    room_type VARCHAR(100),
    total_price INT NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') DEFAULT 'PENDING',
    user_phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (kos_id) REFERENCES kos_properties(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_kos_id (kos_id),
    INDEX idx_status (status),
    INDEX idx_dates (check_in_date, check_out_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
INSERT INTO kos_properties (name, location, price_per_month, rating_value, type, description, is_popular, is_recommended, owner_email, owner_phone) VALUES
('Kos Putri Mama Faiz', 'Limau Manis, Padang', 800000, 4.5, 'PUTRI', 'Kos putri yang nyaman dan aman dengan fasilitas lengkap di area Limau Manis', TRUE, TRUE, 'owner1@example.com', '08123456789'),
('Kos Putra Buk Mina', 'Cupak Tangah, Padang', 650000, 3.8, 'PUTRA', 'Kos putra strategis dekat kampus dengan harga terjangkau', TRUE, TRUE, 'owner2@example.com', '08123456790'),
('Kos Campur Pak Budi', 'Ambacang, Padang', 700000, 4.2, 'CAMPUR', 'Kos campur dengan fasilitas lengkap dan lokasi strategis', TRUE, FALSE, 'owner3@example.com', '08123456791'),
('Kos Putri Melati', 'Pauh, Padang', 750000, 4.4, 'PUTRI', 'Kos putri eksklusif dengan keamanan 24 jam', FALSE, TRUE, 'owner4@example.com', '08123456792'),
('Kos Putra Mawar', 'Padang Selatan', 680000, 4.6, 'PUTRA', 'Kos putra modern dengan fasilitas gym', FALSE, TRUE, 'owner5@example.com', '08123456793'),
('Kos Elite Padang Utara', 'Padang Utara', 950000, 4.8, 'CAMPUR', 'Kos premium dengan fasilitas mewah dan pemandangan kota', FALSE, TRUE, 'owner6@example.com', '08123456794'),
('Kos Sederhana Jati', 'Jati, Padang', 450000, 3.5, 'CAMPUR', 'Kos ekonomis untuk mahasiswa dengan budget terbatas', FALSE, FALSE, 'owner7@example.com', '08123456795'),
('Kos Putri Andalas', 'Limau Manis, Padang', 850000, 4.7, 'PUTRI', 'Kos putri dekat Universitas Andalas dengan fasilitas belajar', FALSE, FALSE, 'owner8@example.com', '08123456796'),
('Kos Putra Minang', 'Koto Tangah, Padang', 600000, 4.0, 'PUTRA', 'Kos putra dengan nuansa tradisional Minang', FALSE, FALSE, 'owner9@example.com', '08123456797'),
('Kos Modern Tabing', 'Tabing, Padang', 720000, 4.3, 'CAMPUR', 'Kos modern dengan rooftop untuk bersantai', FALSE, FALSE, 'owner10@example.com', '08123456798');

-- Insert facilities for kos properties
INSERT INTO kos_facilities (kos_id, name, icon) VALUES
(1, 'WiFi', 'wifi'),
(1, 'AC', 'ac'),
(1, 'Kamar Mandi Dalam', 'bathroom'),
(1, 'Dapur Bersama', 'kitchen'),
(1, 'Parkir Motor', 'parking'),
(2, 'WiFi', 'wifi'),
(2, 'Kamar Mandi Dalam', 'bathroom'),
(2, 'Dapur Bersama', 'kitchen'),
(2, 'Parkir Motor', 'parking'),
(3, 'WiFi', 'wifi'),
(3, 'AC', 'ac'),
(3, 'Kamar Mandi Dalam', 'bathroom'),
(3, 'Laundry', 'laundry'),
(3, 'Parkir Motor', 'parking'),
(4, 'WiFi', 'wifi'),
(4, 'AC', 'ac'),
(4, 'Kamar Mandi Dalam', 'bathroom'),
(4, 'Dapur Bersama', 'kitchen'),
(4, 'Security 24 Jam', 'security'),
(5, 'WiFi', 'wifi'),
(5, 'Kamar Mandi Dalam', 'bathroom'),
(5, 'Dapur Bersama', 'kitchen'),
(5, 'Parkir Motor', 'parking'),
(5, 'Gym', 'gym'),
(6, 'WiFi', 'wifi'),
(6, 'AC', 'ac'),
(6, 'Kamar Mandi Dalam', 'bathroom'),
(6, 'Dapur Pribadi', 'private_kitchen'),
(6, 'Balkon', 'balcony'),
(6, 'Security 24 Jam', 'security'),
(7, 'WiFi', 'wifi'),
(7, 'Kamar Mandi Bersama', 'bathroom'),
(7, 'Dapur Bersama', 'kitchen'),
(8, 'WiFi', 'wifi'),
(8, 'AC', 'ac'),
(8, 'Kamar Mandi Dalam', 'bathroom'),
(8, 'Dapur Bersama', 'kitchen'),
(8, 'Ruang Belajar', 'study'),
(8, 'Security 24 Jam', 'security'),
(9, 'WiFi', 'wifi'),
(9, 'Kamar Mandi Dalam', 'bathroom'),
(9, 'Dapur Bersama', 'kitchen'),
(9, 'Parkir Motor', 'parking'),
(10, 'WiFi', 'wifi'),
(10, 'AC', 'ac'),
(10, 'Kamar Mandi Dalam', 'bathroom'),
(10, 'Dapur Bersama', 'kitchen'),
(10, 'Rooftop', 'rooftop'),
(10, 'Parkir Motor', 'parking');

-- Insert sample reviews
INSERT INTO reviews (kos_id, user_id, rating, comment, is_verified) VALUES
(1, 1, 5.0, 'Kos yang sangat nyaman dan bersih. Pemilik kos juga ramah dan fasilitas lengkap.', TRUE),
(1, 1, 4.0, 'Lokasi strategis dekat kampus. WiFi cepat dan kamar luas.', FALSE),
(2, 1, 4.5, 'Fasilitas AC dan kamar mandi dalam sangat membantu. Recommended!', TRUE),
(3, 1, 3.5, 'Kos lumayan bagus tapi parkir agak sempit.', FALSE),
(4, 1, 5.0, 'Kos terbaik yang pernah saya tempati. Keamanan 24 jam dan lingkungan tenang.', TRUE),
(5, 1, 4.0, 'Harga sesuai dengan fasilitas yang didapat. Dapur bersama cukup luas.', FALSE),
(6, 1, 4.5, 'Lokasi dekat dengan pusat perbelanjaan. Sangat nyaman untuk mahasiswa.', FALSE),
(7, 1, 3.0, 'Kos standar dengan harga terjangkau. Cocok untuk budget mahasiswa.', FALSE);

