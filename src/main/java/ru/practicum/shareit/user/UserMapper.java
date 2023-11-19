package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapper {

    public CreatedUserDto userToCreatedUserDto(User user) {
        return CreatedUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User userDtoToUser(UserDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public User updateUserDtoToUser(UpdateUserDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }
}
