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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Обработка запроса на получение всех пользователей");
        List<User> users = userService.getUsers();
        log.info("Получен список пользователей длиной {}", users.size());
        return users.stream()
                .map(mapper::UserToUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Обработка запроса на получение пользователя с id = {}", userId);
        User user = userService.getUserById(userId);
        log.info("Получен пользователь с id = {}", user.getId());
        return mapper.UserToUserDto(user);
    }

    @PostMapping
    public UserDto createUser(@RequestBody User user) {
        log.info("Обработка запроса на создание пользователя");
        User createdUser = userService.createUser(user);
        log.info("Создан пользователь с id = {}", createdUser.getId());
        return mapper.UserToUserDto(createdUser);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@PathVariable Long userId, @RequestBody User user) {
        log.info("Обработка запроса на частичное обновление пользователя с id = {}", userId);
        User updatedUser = userService.patchUser(userId, user);
        log.info("Обновлены параметры пользователя с id = {}", user.getId());
        return mapper.UserToUserDto(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
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
