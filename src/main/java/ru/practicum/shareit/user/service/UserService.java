package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    CreatedUserDto createUser(UserDto user);

    CreatedUserDto patchUser(long userId, UpdateUserDto user);

    CreatedUserDto getUserById(long id);

    List<CreatedUserDto> getUsers();

    void deleteUserById(long id);

    void deleteUsers();
}
