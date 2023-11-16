package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<CreatedUserDto> getUsers() {
        log.info("Обработка запроса на получение всех пользователей");
        List<CreatedUserDto> users = userService.getUsers();
        log.info("Получен список пользователей длиной {}", users.size());
        return users;
    }

    @GetMapping("/{userId}")
    public CreatedUserDto getUser(@PathVariable @Positive long userId) {
        log.info("Обработка запроса на получение пользователя с id = {}", userId);
        CreatedUserDto user = userService.getUserById(userId);
        log.info("Получен пользователь с id = {}", user.getId());
        return user;
    }

    @PostMapping
    public CreatedUserDto createUser(@RequestBody UserDto dto) {
        log.info("Обработка запроса на создание пользователя");
        CreatedUserDto createdUser = userService.createUser(dto);
        log.info("Создан пользователь с id = {}", createdUser.getId());
        return createdUser;
    }

    @PatchMapping("/{userId}")
    public CreatedUserDto patchUser(@PathVariable @Positive long userId, @RequestBody UpdateUserDto dto) {
        log.info("Обработка запроса на частичное обновление пользователя с id = {}", userId);
        CreatedUserDto updatedUser = userService.patchUser(userId, dto);
        log.info("Обновлены параметры пользователя с id = {}", updatedUser.getId());
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable @Positive long userId) {
        log.info("Обработка запроса на удаление пользователя с id = {}", userId);
        userService.deleteUserById(userId);
        log.info("Пользователь с id = {} удален", userId);
    }

    @DeleteMapping
    public void deleteAllUsers() {
        log.info("Обработка запроса на удаление всех пользователей");
        userService.deleteUsers();
        log.info("Все пользователи удалены");
    }
}
