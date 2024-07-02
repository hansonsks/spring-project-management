-- Create the Roles table
CREATE TABLE IF NOT EXISTS roles (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Create the States table
CREATE TABLE IF NOT EXISTS states (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Create the Users table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INTEGER,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Create the Todos table
CREATE TABLE IF NOT EXISTS todos (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    owner_id INTEGER,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Create the Tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    priority VARCHAR(255),
    state_id INTEGER,
    todo_id INTEGER,
    FOREIGN KEY (todo_id) REFERENCES todos(id),
    FOREIGN KEY (state_id) REFERENCES states(id)
);

-- Create the Todos_Collaborators table
CREATE TABLE IF NOT EXISTS todos_collaborators (
    todo_id INTEGER NOT NULL,
    collaborator_id INTEGER NOT NULL,
    PRIMARY KEY (todo_id, collaborator_id),
    FOREIGN KEY (todo_id) REFERENCES todos(id),
    FOREIGN KEY (collaborator_id) REFERENCES users(id)
);