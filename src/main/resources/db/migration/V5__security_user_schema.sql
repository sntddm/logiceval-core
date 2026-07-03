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

-- 3. Seed an initial admin and test account (Notice: NO hardcoded IDs)
INSERT INTO
    system_users (username, password_hash)
VALUES (
        'admin',
        '$2a$10$8bjMFaTfi3/GQ5c56hkWsuytHMQdgg2kknivy7dOMfl/.W62723UG'
    ),
    (
        'user',
        '$2a$10$8bjMFaTfi3/GQ5c56hkWsuytHMQdgg2kknivy7dOMfl/.W62723UG'
    );

-- 4. Map roles by querying the newly generated IDs dynamically
INSERT INTO
    user_roles (user_id, role_name)
VALUES (
        (
            SELECT id
            FROM system_users
            WHERE
                username = 'admin'
        ),
        'ROLE_ADMIN'
    ),
    (
        (
            SELECT id
            FROM system_users
            WHERE
                username = 'admin'
        ),
        'ROLE_USER'
    ),
    (
        (
            SELECT id
            FROM system_users
            WHERE
                username = 'user'
        ),
        'ROLE_USER'
    );