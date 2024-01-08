package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class UserServiceImplIntTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserServiceImpl userService;

    @Test
    void createUser_whenDataIsCorrect_thenSaveUser() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("name")
                .email("email")
                .build();

        UserDto createdUserDto = userService.createUser(userCreateDto);

        assertNotNull(createdUserDto);
        assertNotNull(createdUserDto.getId());
        assertEquals(userCreateDto.getName(), createdUserDto.getName());
        assertEquals(userCreateDto.getEmail(), createdUserDto.getEmail());
    }

    @Test
    void patchUser_whenDataIsCorrect_thenUpdateUser() {
        User user = User.builder()
                .name("name")
                .email("nice@email.com")
                .build();
        user = userRepository.save(user);

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Updated Name")
                .build();

        UserDto updatedUserDto = userService.patchUser(user.getId(), userUpdateDto);

        assertNotNull(updatedUserDto);
        assertEquals(user.getId(), updatedUserDto.getId());
        assertEquals(userUpdateDto.getName(), updatedUserDto.getName());
        assertEquals(user.getEmail(), updatedUserDto.getEmail());
    }

    @Test
    void getUserById_whenDataIsCorrect_thenReturnUser() {
        User user = User.builder()
                .name("name")
                .email("nice@email.com")
                .build();
        user = userRepository.save(user);

        UserDto foundUserDto = userService.getUserById(user.getId());

        assertNotNull(foundUserDto);
        assertEquals(user.getId(), foundUserDto.getId());
        assertEquals(user.getName(), foundUserDto.getName());
        assertEquals(user.getEmail(), foundUserDto.getEmail());
    }

    @Test
    void getUsers_whenDataIsCorrect_thenReturnUsers() {
        userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        userRepository.save(User.builder()
                .name("name")
                .email("nice2@email.com")
                .build());

        List<UserDto> userDtoList = userService.getUsers();

        assertNotNull(userDtoList);
        assertEquals(2, userDtoList.size());
    }

    @Test
    void deleteUserById_whenDataIsCorrect_thenDeleteUser() {
        User user = User.builder()
                .name("name")
                .email("nice@email.com")
                .build();
        user = userRepository.save(user);

        userService.deleteUserById(user.getId());

        assertFalse(userRepository.existsById(user.getId()));
    }

    @Test
    void deleteUsers_whenDataIsCorrect_thenDeleteAllUsers() {
        userRepository.save(User.builder()
                .name("name")
                .email("nice@email.com")
                .build());
        userRepository.save(User.builder()
                .name("name")
                .email("nice2@email.com")
                .build());

        userService.deleteUsers();

        assertEquals(0, userRepository.count());
    }
}
