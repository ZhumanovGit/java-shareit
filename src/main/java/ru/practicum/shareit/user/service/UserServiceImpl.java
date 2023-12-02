package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserMapper mapper;


    @Override
    @Transactional
    public UserDto createUser(UserCreateDto userCreateDto) {
        User user = mapper.userCreateDtoToUser(userCreateDto);
        User createdUser = userRepository.save(user);
        return mapper.userToUserDto(createdUser);
    }

    @Override
    @Transactional
    public UserDto patchUser(long userId, @NonNull UserUpdateDto userUpdates) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " не существует"));
        if (userUpdates.getName() == null) {
            userUpdates.setName(user.getName());
        }

        if (userUpdates.getEmail() == null) {
            userUpdates.setEmail(user.getEmail());
        }
        User userForUpdate = mapper.userUpdateDtoToUser(userUpdates);
        userForUpdate.setId(userId);

        userRepository.save(userForUpdate);
        return mapper.userToUserDto(userForUpdate);

    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + id + " не существует"));
        return mapper.userToUserDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(mapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(long id) {
        commentRepository.deleteAllByAuthorId(id);
        itemRepository.deleteAllByOwnerId(id);
        userRepository.deleteById(id);
    }

    @Override
    public void deleteUsers() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

}
