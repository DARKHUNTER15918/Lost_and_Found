-- ====================================================================
-- Campus Lost & Found Management System - Database Setup Script
-- Database: MySQL 8.0+
-- ====================================================================

CREATE DATABASE IF NOT EXISTS campus_lostfound_db;
USE campus_lostfound_db;

-- 1. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    icon VARCHAR(50)
);

-- 2. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    student_id VARCHAR(50),
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_STUDENT',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Item Reports Table (Lost / Found)
CREATE TABLE IF NOT EXISTS item_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    category_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(150) NOT NULL,
    item_date DATE NOT NULL,
    image_url VARCHAR(255),
    report_type ENUM('LOST', 'FOUND') NOT NULL,
    status ENUM('OPEN', 'CLAIMED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    posted_by_user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    CONSTRAINT fk_item_user FOREIGN KEY (posted_by_user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Claim Requests Table
CREATE TABLE IF NOT EXISTS claim_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_report_id BIGINT NOT NULL,
    claimer_user_id BIGINT NOT NULL,
    claim_description TEXT NOT NULL,
    proof_details TEXT NOT NULL,
    contact_info VARCHAR(150) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    admin_remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_claim_item FOREIGN KEY (item_report_id) REFERENCES item_reports(id) ON DELETE CASCADE,
    CONSTRAINT fk_claim_user FOREIGN KEY (claimer_user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Default Sample Data Insertion
-- Insert Default Categories
INSERT INTO categories (name, description, icon) VALUES 
('Electronics & Gadgets', 'Laptops, chargers, headphones, smartphones, tablets, USB drives', 'bi-laptop'),
('IDs, Cards & Documents', 'College ID cards, driver licenses, ATM cards, certificates, documents', 'bi-card-heading'),
('Books & Stationery', 'Textbooks, notebooks, calculators, stationery pouches, art supplies', 'bi-book'),
('Keys & Wallets', 'Room keys, vehicle keys, wallets, coin purses', 'bi-wallet2'),
('Bags & Backpacks', 'College bags, laptop bags, pouches, gym bags', 'bi-backpack'),
('Clothing & Wearables', 'Jackets, sweaters, caps, watches, jewelry, glasses', 'bi-watch'),
('Sports & Fitness', 'Sports gear, badminton rackets, water bottles, gym accessories', 'bi-bicycle'),
('Others & Miscellaneous', 'Umbrellas, lunchboxes, personal accessories', 'bi-box-seam')
ON DUPLICATE KEY UPDATE name=name;

-- Note: The Spring Boot application includes a DataInitializer that automatically seeds
-- default accounts and sample reports on startup:
-- Admin: admin@campus.edu / admin123
-- Student: student@campus.edu / student123
