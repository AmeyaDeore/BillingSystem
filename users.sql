CREATE DATABASE IF NOT EXISTS electricity_billing;
USE electricity_billing;

CREATE TABLE IF NOT EXISTS users (
  username VARCHAR(50) PRIMARY KEY,
  password VARCHAR(50) NOT NULL
);

INSERT INTO users (username, password)
VALUES ('admin', '12345')
ON DUPLICATE KEY UPDATE password='12345';