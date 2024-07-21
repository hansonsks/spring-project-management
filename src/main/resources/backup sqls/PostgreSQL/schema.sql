CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS states (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS todos (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    owner_id BIGINT,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    priority VARCHAR(255),
    state_id BIGINT,
    todo_id BIGINT,
    assigned_user_id BIGINT,
    FOREIGN KEY (todo_id) REFERENCES todos(id),
    FOREIGN KEY (state_id) REFERENCES states(id),
    FOREIGN KEY (assigned_user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS todos_collaborators (
    todo_id BIGINT NOT NULL,
    collaborator_id BIGINT NOT NULL,
    PRIMARY KEY (todo_id, collaborator_id),
    FOREIGN KEY (todo_id) REFERENCES todos(id),
    FOREIGN KEY (collaborator_id) REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tasks_collaborators (
    task_id BIGINT NOT NULL,
    collaborator_id BIGINT NOT NULL,
    PRIMARY KEY (task_id, collaborator_id),
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (collaborator_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS oauth_users (
    id SERIAL PRIMARY KEY,
    provider VARCHAR(255) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id SERIAL PRIMARY KEY,
    comment VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_edited BOOLEAN NOT NULL,
    task_id BIGINT,
    user_id BIGINT,
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);