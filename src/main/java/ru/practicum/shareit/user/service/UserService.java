package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User patchUser(long userId, User user);

    User getUserById(long id);

    List<User> getUsers();

    void deleteUserById(long id);

    void deleteUsers();
}
