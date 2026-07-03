CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

CREATE TABLE staff (
    staff_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    specialization VARCHAR(255) NOT NULL,
    availability BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE patients (
    patient_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(20) NOT NULL,
    diagnosis TEXT NOT NULL,
    admission_date DATETIME DEFAULT NULL,
    discharge_date DATETIME DEFAULT NULL,
    current_status VARCHAR(50) NOT NULL,
    assigned_doctor_id BIGINT DEFAULT NULL,
    FOREIGN KEY (assigned_doctor_id) REFERENCES staff(staff_id)
);

-- Insert some dummy staff to get started
INSERT INTO staff (name, role, specialization, availability) VALUES
('Alice Smith', 'DOCTOR', 'Cardiology', true),
('Bob Jones', 'DOCTOR', 'Neurology', true),
('Charlie Brown', 'NURSE', 'Pediatrics', true);
