package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {
    UserMapper mapper = new UserMapper();

    @Test
    public void userCreateDtoToUserTest() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("test")
                .email("testEmail@email.com")
                .build();

        User user = mapper.userCreateDtoToUser(dto);

        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }

    @Test
    public void userUpdateDtoToUserTest() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name("test")
                .email("testEmail@email.com")
                .build();

        User user = mapper.userUpdateDtoToUser(dto);

        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }

    @Test
    public void userToUserDtoTest() {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("testEmail@ya.com")
                .build();

        UserDto dto = mapper.userToUserDto(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

}