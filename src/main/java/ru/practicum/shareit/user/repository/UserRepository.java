package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User createUser(User user);

    void updateUser(User user);

    Optional<User> getUserById(long id);

    List<User> getUsers();

    void deleteUserById(long id);

    void deleteAllUsers();
}
