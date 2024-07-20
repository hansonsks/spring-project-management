-- Commented this part of the script out for tests
-- The same code will be ran inside scheduled_reset.sql anyways
-- SET FOREIGN_KEY_CHECKS = 0;
--
--     -- Truncate tables
-- TRUNCATE TABLE oauth_users;
-- TRUNCATE TABLE roles;
-- TRUNCATE TABLE states;
-- TRUNCATE TABLE tasks;
-- TRUNCATE TABLE todos;
-- TRUNCATE TABLE todos_collaborators;
-- TRUNCATE TABLE users;
--
-- -- Reset auto-increment values
-- ALTER TABLE roles AUTO_INCREMENT = 1;
-- ALTER TABLE users AUTO_INCREMENT = 1;
-- ALTER TABLE states AUTO_INCREMENT = 1;
-- ALTER TABLE tasks AUTO_INCREMENT = 1;
-- ALTER TABLE todos AUTO_INCREMENT = 1;
-- ALTER TABLE todos_collaborators AUTO_INCREMENT = 1;
-- ALTER TABLE oauth_users AUTO_INCREMENT = 1;
--
-- -- Re-enable foreign key checks
-- SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');

INSERT INTO users (first_name, last_name, email, password, role_id)
VALUES
    -- Admin Password: Admin123?
    ('Admin',  'Doe', 'admin@mail.com', '$2a$10$oSJ00.BgokS3L96e0x4VKOKtilabLz.lLgHsvz5tVgKbt5hrG/Mvu', 1),

    -- All User Passwords: User123?
    ('User',   'Doe', 'user@mail.com',  '$2a$10$W9FNNXcqGD6QQ0YE4wAcSO5sxOf9BF8leP3T1EEARmM.bHiChU2uG', 2),
    ('Guest', 'Doe', 'guest@mail.com', '$2a$10$77co3ie.zQsgUVfDvswRF.ES3Nkv6fUzg9Gje7r83jnj6kO/nSRQK', 1),
    ('User One', 'Doe', 'user1@mail.com', '$2a$10$W9FNNXcqGD6QQ0YE4wAcSO5sxOf9BF8leP3T1EEARmM.bHiChU2uG', 2),
    ('User Two', 'Doe', 'user2@mail.com', '$2a$10$W9FNNXcqGD6QQ0YE4wAcSO5sxOf9BF8leP3T1EEARmM.bHiChU2uG', 2),
    ('User Three', 'Doe', 'user3@mail.com', '$2a$10$W9FNNXcqGD6QQ0YE4wAcSO5sxOf9BF8leP3T1EEARmM.bHiChU2uG', 2),

    -- GitHubUser Password: GitHubUser123?
    ('GitHubUser', 'Doe', 'githubuser@mail.com', '$2a$10$JMAqfLKYASu6cpcV..HQzeLxquiKTvd5F4e/QPnUoZ/cPIkE3oRF2', 1);

INSERT INTO states (name) VALUES
                              ('New'),
                              ('In Progress'),
                              ('Under Review'),
                              ('Completed');

INSERT INTO todos (title, description, created_at, owner_id)
VALUES
    ('Admin Todo #1', 'Demo Description', NOW(), 1),
    ('Admin Todo #2', 'Demo Description', NOW(), 1),
    ('Admin Todo #3', 'Demo Description', NOW(), 1),
    ('User Todo #1',  'Demo Description', NOW(), 2),
    ('User Todo #2',  'Demo Description', NOW(), 2),
    ('User Todo #3',  'Demo Description', NOW(), 2),
    ('Guest Todo #1', 'Demo Description', NOW(), 3),
    ('Guest Todo #2', 'Demo Description', NOW(), 3),
    ('Guest Todo #3', 'Demo Description', NOW(), 3);

INSERT INTO tasks (name, description, priority, todo_id, state_id)
VALUES
    ('Trivial Task',           'Demo Description', 'TRIVIAL',  1, 4),
    ('Low Priority Task',      'Demo Description', 'LOW',      1, 1),
    ('Medium Priority Task',   'Demo Description', 'MEDIUM',   1, 2),
    ('High Priority Task',     'Demo Description', 'HIGH',     1, 2),
    ('Urgent Task',            'Demo Description', 'URGENT',   1, 3),

    ('Trivial Task',           'Demo Description', 'TRIVIAL',  2, 1),
    ('Trivial Task',           'Demo Description', 'TRIVIAL',  2, 1),
    ('Trivial Task',           'Demo Description', 'TRIVIAL',  2, 1),

    ('Low Priority Task',      'Demo Description', 'LOW',      3, 1),
    ('Low Priority Task',      'Demo Description', 'LOW',      3, 1),
    ('Low Priority Task',      'Demo Description', 'LOW',      3, 1),

    ('Trivial Task',           'Demo Description', 'TRIVIAL',  4, 2),
    ('Low Priority Task',      'Demo Description', 'LOW',      4, 4),
    ('Medium Priority Task',   'Demo Description', 'MEDIUM',   4, 3),
    ('High Priority Task',     'Demo Description', 'HIGH',     4, 3),
    ('Urgent Task',            'Demo Description', 'URGENT',   4, 2),

    ('Medium Priority Task',   'Demo Description', 'MEDIUM',   5, 1),
    ('Medium Priority Task',   'Demo Description', 'MEDIUM',   5, 2),
    ('Medium Priority Task',   'Demo Description', 'MEDIUM',   5, 3),

    ('High Priority Task',     'Demo Description', 'HIGH',     6, 3),
    ('High Priority Task',     'Demo Description', 'HIGH',     6, 2),
    ('Urgent Priority Task',   'Demo Description', 'URGENT',   6, 3),

    ('Trivial Task',           'Demo Description', 'TRIVIAL',  7, 4),
    ('Low Priority Task',      'Demo Description', 'LOW',      7, 1),
    ('Medium Priority Task',   'Demo Description', 'MEDIUM',   7, 2),
    ('High Priority Task',     'Demo Description', 'HIGH',     7, 2),
    ('Urgent Task',            'Demo Description', 'URGENT',   7, 3);

INSERT INTO todos_collaborators (todo_id, collaborator_id)
VALUES
    (1, 2),
    (1, 4);

INSERT INTO oauth_users (provider, provider_user_id, user_id)
VALUES
    ('GITHUB', 'github12345', 7);

-- TODO: Add sample comments to sample tasks

INSERT INTO comments (comment, user_id, task_id, created_at, is_edited)
VALUES
    ('Sample Comment', 1, 1, NOW(), FALSE);