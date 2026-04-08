-- Test data for integration tests
-- Insert test users with encoded passwords (password = 'password')
-- Passwords are BCrypt encoded: $2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQhQjGxCtkQ1kphH1ytoY1eqsKu (password)

-- Insert test users (assumes fresh H2 database created by Hibernate)
INSERT INTO users (id, username, password, role) VALUES 
(1, 'maker1', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQhQjGxCtkQ1kphH1ytoY1eqsKu', 'MAKER'),
(2, 'checker1', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQhQjGxCtkQ1kphH1ytoY1eqsKu', 'CHECKER'),
(3, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQhQjGxCtkQ1kphH1ytoY1eqsKu', 'MAKER,CHECKER');
