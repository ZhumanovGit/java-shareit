package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.CreatedUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserMapper mapper;


    @Override
    public CreatedUserDto createUser(UserDto userDto) {
        User user = mapper.userDtoToUser(userDto);
        User createdUser = userRepository.createUser(user);
        return mapper.userToCreatedUserDto(createdUser);
    }

    @Override
    public CreatedUserDto patchUser(long userId, @NonNull UpdateUserDto userUpdates) {

        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " не существует"));
        if (userUpdates.getName() == null) {
            userUpdates.setName(user.getName());
        }

        if (userUpdates.getEmail() == null) {
            userUpdates.setEmail(user.getEmail());
        }
        User userForUpdate = mapper.updateUserDtoToUser(userUpdates);
        userForUpdate.setId(userId);

        userRepository.updateUser(userForUpdate);
        return mapper.userToCreatedUserDto(userForUpdate);

    }

    @Override
    public CreatedUserDto getUserById(long id) {
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + id + " не существует"));
        return mapper.userToCreatedUserDto(user);
    }

    @Override
    public List<CreatedUserDto> getUsers() {
        return userRepository.getUsers().stream()
                .map(mapper::userToCreatedUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(long id) {
        itemRepository.deleteAllItemsByOwnerId(id);
        userRepository.deleteUserById(id);
    }

    @Override
    public void deleteUsers() {
        itemRepository.deleteAllItems();
        userRepository.deleteAllUsers();
    }

}
