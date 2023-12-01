package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    UserRepository userRepository;
    ItemRepository itemRepository;
    CommentRepository commentRepository;
    UserServiceImpl service;

    UserMapper mapper;

    @BeforeEach
    public void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        commentRepository = mock(CommentRepository.class);
        mapper = new UserMapper();
        service = new UserServiceImpl(userRepository, itemRepository, commentRepository, mapper);
    }

    void assertEqualsUser(UserDto o1, UserDto o2) {
        assertEquals(o1.getId(), o2.getId());
        assertEquals(o1.getName(), o2.getName());
        assertEquals(o1.getEmail(), o2.getEmail());
    }

    @Test
    public void createUser_whenUserPassValidation_thenReturnNewUser() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("test")
                .email("testEmail@ya.com")
                .build();
        User user = mapper.userCreateDtoToUser(dto);
        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        when(userRepository.save(any())).thenReturn(User.builder()
                .id(1L)
                .name(user.getName())
                .email(user.getEmail())
                .build());

        UserDto actualUser = service.createUser(dto);

        assertEqualsUser(expectedUser, actualUser);
    }

    @Test
    public void patchUser_whenChangesAreCorrect_thenUpdateUser() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        UserUpdateDto userUpdates = UserUpdateDto.builder().name("newTest").email("newEmail@ya.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto updatedUser = service.patchUser(user.getId(), userUpdates);

        assertEquals(user.getId(), updatedUser.getId());
        assertEquals(userUpdates.getName(), updatedUser.getName());
        assertEquals(userUpdates.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void getUserById_whenUserWasFound_thenReturnUser() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        UserDto expectedUser = mapper.userToUserDto(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto actualUser = service.getUserById(user.getId());

        assertEqualsUser(expectedUser, actualUser);
    }

    @Test
    public void getUserById_whenUserWasNotFound_thenThrowUserNotFoundException() {
        String expectedResponse = "Пользователя с id = 5 не существует";

        Throwable throwable = assertThrows(NotFoundException.class, () -> service.getUserById(5L));

        assertEquals(expectedResponse, throwable.getMessage());
    }

    @Test
    public void getUsers_whenUsersIsEmpty_thenReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserDto> users = service.getUsers();

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
        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));

        List<UserDto> users = service.getUsers();

        assertEquals(3, users.size());
    }
}