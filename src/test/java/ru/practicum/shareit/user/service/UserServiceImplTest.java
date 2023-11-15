package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.model.UserEmailIsAlreadyExists;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.exception.model.UserValidateException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserValidator;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    UserRepository userRepository;
    ItemRepository itemRepository;
    UserServiceImpl service;

    @BeforeEach
    public void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        service = new UserServiceImpl(userRepository, itemRepository, new UserValidator());
    }

    void assertEqualsUser(User o1, User o2) {
        assertEquals(o1.getId(), o2.getId());
        assertEquals(o1.getName(), o2.getName());
        assertEquals(o1.getEmail(), o2.getEmail());
    }

    @Test
    public void createUser_whenUserPassValidation_thenReturnNewUser() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        when(userRepository.isEmailBooked(user)).thenReturn(false);
        when(userRepository.createUser(user)).thenReturn(User.builder()
                .id(1L)
                .name(user.getName())
                .email(user.getEmail())
                .build());

        User actualUser = service.createUser(user);

        assertEqualsUser(user, actualUser);
    }

    @Test
    public void createUser_whenUserInCorrect_thenThrowValidationError() {
        User user = User.builder()
                .id(1L)
                .email("testEmail@ya.com")
                .build();
        String expectedResponse = "Имя не может быть пустым";

        Throwable throwable = assertThrows(UserValidateException.class, () -> service.createUser(user));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void createUser_whenEmailAlreadyExists_thenThrowUserEmailExistsException() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        String expectedResponse = "Пользователь с данной почтой уже зарегистрирован в системе";
        when(userRepository.isEmailBooked(user)).thenReturn(true);

        Throwable throwable = assertThrows(UserEmailIsAlreadyExists.class, () -> service.createUser(user));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void patchUser_whenChangesAreCorrect_thenUpdateUser() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        User userUpdates = User.builder().id(1L).name("newTest").email("newEmail@ya.com").build();
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(userRepository.isEmailBooked(userUpdates)).thenReturn(false);

        User updatedUser = service.patchUser(1L, userUpdates);

        assertEquals(user.getId(), userUpdates.getId());
        assertEquals(userUpdates.getName(), updatedUser.getName());
        assertEquals(userUpdates.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void patchUser_whenChangesArentCorrect_thenThrowValidateException() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        User userUpdates = User.builder().id(1L).name("").email("newEmail@ya.com").build();
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(userRepository.isEmailBooked(userUpdates)).thenReturn(false);
        String expectedResponse = "Имя не может быть пустым";

        Throwable throwable = assertThrows(UserValidateException.class, () -> service.patchUser(1L, userUpdates));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void patchUser_whenEmailIsTaken_thenThrowEmailExistsException() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        User userUpdates = User.builder().id(1L).name("asd").email("newEmail@ya.com").build();
        when(userRepository.getUserById(1L)).thenReturn(Optional.of(user));
        when(userRepository.isEmailBooked(userUpdates)).thenReturn(true);
        String expectedResponse = "Пользователь с данной почтой уже зарегистрирован в системе";

        Throwable throwable = assertThrows(UserEmailIsAlreadyExists.class, () -> service.patchUser(1L, userUpdates));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getUserById_whenUserWasFound_thenReturnUser() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        when(userRepository.getUserById(user.getId())).thenReturn(Optional.of(user));

        User actualUser = service.getUserById(user.getId());

        assertEqualsUser(user, actualUser);
    }

    @Test
    public void getUserById_whenIdIsntCorrect_thenThrowValidateException() {
        String expectedResponse = "id пользователя не может быть отрицательным";

        Throwable throwable = assertThrows(UserValidateException.class, () -> service.getUserById(-1L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getUserById_whenUserWasNotFound_thenThrowUserNotFoundException() {
        String expectedResponse = "Пользователя с id = 5 не существует";

        Throwable throwable = assertThrows(UserNotFoundException.class, () -> service.getUserById(5L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getUsers_whenUsersIsEmpty_thenReturnEmptyList() {
        when(userRepository.getUsers()).thenReturn(new ArrayList<>());

        List<User> users = service.getUsers();

        assertEquals(0, users.size());
    }

    @Test
    public void getUsers_whenUsersIsNotEmpty_thenReturnListOfUsers() {
        User user1 = User.builder()
                .id(1L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        User user2 = User.builder()
                .id(2L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        User user3 = User.builder()
                .id(3L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        when(userRepository.getUsers()).thenReturn(List.of(user1, user2, user3));

        List<User> users = service.getUsers();

        assertEquals(3, users.size());
    }
}