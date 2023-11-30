package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
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

    UserMapper mapper;

    @BeforeEach
    public void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        mapper = new UserMapper();
        service = new UserServiceImpl(userRepository, itemRepository, mapper);
    }

    void assertEqualsUser(CreatedUserDto o1, CreatedUserDto o2) {
        assertEquals(o1.getId(), o2.getId());
        assertEquals(o1.getName(), o2.getName());
        assertEquals(o1.getEmail(), o2.getEmail());
    }

    @Test
    public void createUser_whenUserPassValidation_thenReturnNewUser() {
        UserDto dto = UserDto.builder()
                .name("test")
                .email("testEmail@ya.com")
                .build();
        User user = mapper.userDtoToUser(dto);
        CreatedUserDto expectedUser = CreatedUserDto.builder()
                .id(1L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        when(userRepository.save(user)).thenReturn(User.builder()
                .id(1L)
                .name(user.getName())
                .email(user.getEmail())
                .build());

        CreatedUserDto actualUser = service.createUser(dto);

        assertEqualsUser(expectedUser, actualUser);
    }

    @Test
    public void patchUser_whenChangesAreCorrect_thenUpdateUser() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("testEmail@ya.com")
                .build();
        UpdateUserDto userUpdates = UpdateUserDto.builder().name("newTest").email("newEmail@ya.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        CreatedUserDto updatedUser = service.patchUser(user.getId(), userUpdates);

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
        CreatedUserDto expectedUser = mapper.userToCreatedUserDto(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        CreatedUserDto actualUser = service.getUserById(user.getId());

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

        List<CreatedUserDto> users = service.getUsers();

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

        List<CreatedUserDto> users = service.getUsers();

        assertEquals(3, users.size());
    }
}