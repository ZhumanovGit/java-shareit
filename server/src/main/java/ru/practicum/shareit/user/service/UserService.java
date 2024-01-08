package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserCreateDto user);

    UserDto patchUser(long userId, UserUpdateDto user);

    UserDto getUserById(long id);

    List<UserDto> getUsers();

    void deleteUserById(long id);

    void deleteUsers();
}
