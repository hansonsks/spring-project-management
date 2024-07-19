-- SET CONSTRAINTS ALL DEFERRED;
--
-- -- Truncate tables
-- TRUNCATE TABLE oauth_users CASCADE;
-- TRUNCATE TABLE roles CASCADE;
-- TRUNCATE TABLE states CASCADE;
-- TRUNCATE TABLE tasks CASCADE;
-- TRUNCATE TABLE todos CASCADE;
-- TRUNCATE TABLE todos_collaborators CASCADE;
-- TRUNCATE TABLE users CASCADE;
--
-- -- Reset sequence values (PostgreSQL equivalent of AUTO_INCREMENT)
-- ALTER SEQUENCE roles_id_seq RESTART WITH 1;
-- ALTER SEQUENCE users_id_seq RESTART WITH 1;
-- ALTER SEQUENCE states_id_seq RESTART WITH 1;
-- ALTER SEQUENCE tasks_id_seq RESTART WITH 1;
-- ALTER SEQUENCE todos_id_seq RESTART WITH 1;
-- ALTER SEQUENCE todos_collaborators_id_seq RESTART WITH 1;
-- ALTER SEQUENCE oauth_users_id_seq RESTART WITH 1;
--
-- -- Re-enable foreign key checks
-- SET CONSTRAINTS ALL IMMEDIATE;

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

INSERT INTO tasks (name, description, priority, todo_id, state_id, assigned_user_id)
VALUES
    ('Trivial Task',           'Demo Description', 'TRIVIAL',  1, 4, NULL),
    ('Low Priority Task',      'Demo Description', 'LOW',      1, 1, NULL),
    ('Medium Priority Task',   'Demo Description', 'MEDIUM',   1, 2, NULL),
    ('High Priority Task',     'Demo Description', 'HIGH',     1, 2, NULL),
    ('Urgent Task',            'Demo Description', 'URGENT',   1, 3, NULL),

    ('Trivial Task',           'Demo Description', 'TRIVIAL',  2, 1, NULL),
    ('Trivial Task',           'Demo Description', 'TRIVIAL',  2, 1, NULL),
    ('Trivial Task',           'Demo Description', 'TRIVIAL',  2, 1, NULL),

    ('Low Priority Task',      'Demo Description', 'LOW',      3, 1, NULL),
    ('Low Priority Task',      'Demo Description', 'LOW',      3, 1, NULL),
    ('Low Priority Task',      'Demo Description', 'LOW',      3, 1, NULL),

    ('Trivial Task',           'Demo Description', 'TRIVIAL',  4, 2, NULL),
    ('Low Priority Task',      'Demo Description', 'LOW',      4, 4, NULL),
    ('Medium Priority Task',   'Demo Description', 'MEDIUM',   4, 3, NULL),
    ('High Priority Task',     'Demo Description', 'HIGH',     4, 3, NULL),
    ('Urgent Task',            'Demo Description', 'URGENT',   4, 2, NULL),

    ('Medium Priority Task',   'Demo Description', 'MEDIUM',   5, 1, NULL),
    ('Medium Priority Task',   'Demo Description', 'MEDIUM',   5, 2, NULL),
    ('Medium Priority Task',   'Demo Description', 'MEDIUM',   5, 3, NULL),

    ('High Priority Task',     'Demo Description', 'HIGH',     6, 3, NULL),
    ('High Priority Task',     'Demo Description', 'HIGH',     6, 2, NULL),
    ('Urgent Priority Task',   'Demo Description', 'URGENT',   6, 3, NULL),

    ('Trivial Task',           'Demo Description', 'TRIVIAL',  7, 4, NULL),
    ('Low Priority Task',      'Demo Description', 'LOW',      7, 1, NULL),
    ('Medium Priority Task',   'Demo Description', 'MEDIUM',   7, 2, NULL),
    ('High Priority Task',     'Demo Description', 'HIGH',     7, 2, NULL),
    ('Urgent Task',            'Demo Description', 'URGENT',   7, 3, NULL);

INSERT INTO todos_collaborators (todo_id, collaborator_id)
VALUES
    (1, 2),
    (1, 4);

INSERT INTO oauth_users (provider, provider_user_id, user_id)
VALUES
    ('github', 'github12345', 6);

-- TODO: Add sample comments to sample tasks

INSERT INTO comments (comment, user_id, task_id, created_at, is_edited)
VALUES
    ('Sample Comment', 1, 1, NOW(), FALSE);