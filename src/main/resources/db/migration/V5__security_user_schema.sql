-- 1. Create a table to track authenticated accounts
CREATE TABLE IF NOT EXISTS system_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- 2. Create a table to track explicit authority string roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role_name),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES system_users (id) ON DELETE CASCADE
);

-- 3. Seed an initial admin and test account (Password is 'password123' hashed with BCrypt)
INSERT INTO
    system_users (id, username, password_hash)
VALUES (
        1,
        'admin',
        '$2a$10$dXJ3SWp0BBvX8b3T10bTFe3S7gB2O6IeVvbywWhD1Lbe3N4K2r6/G'
    ),
    (
        2,
        'user',
        '$2a$10$dXJ3SWp0BBvX8b3T10bTFe3S7gB2O6IeVvbywWhD1Lbe3N4K2r6/G'
    );

INSERT INTO
    user_roles (user_id, role_name)
VALUES (1, 'ROLE_ADMIN'),
    (1, 'ROLE_USER'),
    (2, 'ROLE_USER');