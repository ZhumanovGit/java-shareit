INSERT INTO users (id, name, email, email_lowercase)
VALUES (1, 'owner', 'owner@mail.ru', 'owner@mail.ru'), (2, 'user', 'user@mail.ru', 'user@mail.ru');

INSERT INTO requests (id, description, owner_id, created)
VALUES (1, 'request1', 2, '2022-01-01T00:00:00');

INSERT INTO items (id, owner_id, name, description, request_id, available)
VALUES (1, 1, 'item1', 'desc',  null, true),
       (2, 1, 'item2', 'desc',  1, true),
       (3, 1, 'item3', 'desc',  1, true);
