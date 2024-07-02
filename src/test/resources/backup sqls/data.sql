-- Insert into Roles
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');

-- Insert into Users
INSERT INTO users (first_name, last_name, email, password, role_id)
VALUES
  ('Nick', 'Green', 'nick@mail.com', '$2a$10$CJgEoobU2gm0euD4ygru4ukBf9g8fYnPrMvYk.q0GMfOcIDtUhEwC', 2),
  ('Nora', 'White', 'nora@mail.com', '$2a$10$yYQaJrHzjOgD5wWCyelp0e1Yv1KEKeqUlYfLZQ1OQvyUrnEcX/rOy', 2),
  ('Mike', 'Brown', 'mike@mail.com', '$2a$10$CdEJ2PKXgUCIwU4pDQWICuiPjxb1lysoX7jrN.Y4MTMoY9pjfPALO', 1);

-- Insert into States
INSERT INTO states (name) VALUES ('New');
INSERT INTO states (name) VALUES ('In Progress');
INSERT INTO states (name) VALUES ('Under Review');
INSERT INTO states (name) VALUES ('Completed');

-- Insert into Todos
INSERT INTO todos (title, created_at, owner_id)
VALUES
  ('Mike''s To-Do #1', '2020-09-16 14:00:04.810221', 3),
  ('Mike''s To-Do #2', '2020-09-16 14:00:11.480271', 3),
  ('Mike''s To-Do #3', '2020-09-16 14:00:16.351238', 3),
  ('Nick''s To-Do #1', '2020-09-16 14:14:54.532337', 1),
  ('Nick''s To-Do #2', '2020-09-16 14:15:04.707176', 1),
  ('Nora''s To-Do #1', '2020-09-16 14:15:32.464391', 2),
  ('Nora''s To-Do #2', '2020-09-16 14:15:39.16246', 2);

-- Insert into Tasks
INSERT INTO tasks (name, priority, todo_id, state_id)
VALUES
  ('Task #2', 'LOW', 7, 1),
  ('Task #1', 'HIGH', 7, 4),
  ('Task #3', 'MEDIUM', 7, 2);

-- Insert into Todos_Collaborators
INSERT INTO todos_collaborators (todo_id, collaborator_id)
VALUES
  (1, 1),
  (1, 2),
  (4, 2),
  (4, 3),
  (6, 1),
  (6, 3);