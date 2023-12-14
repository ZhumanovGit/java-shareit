INSERT INTO users (id, name, email, email_lowercase)
VALUES (1, 'owner', 'owner@mail.ru', 'owner@mail.ru'), (2, 'user', 'user@mail.ru', 'user@mail.ru');

INSERT INTO requests (id, description, owner_id, created)
VALUES (1, 'request1', 2, '2022-01-01T00:00:00'),
       (2, 'request2', 1, '2022-01-02T00:00:00'),
       (3, 'request3', 2, '2022-01-03T00:00:00');

INSERT INTO items (id, owner_id, name, description, available, request_id)
VALUES (1, 1, 'item1', 'item1', true, null), (2, 1, 'item2', 'item2', false, null), (3, 1, 'item3', 'item3', true, 1);