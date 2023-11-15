package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.UserEmailIsAlreadyExists;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.exception.model.UserValidateException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserValidator;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserValidator validator;

    @Override
    public User createUser(User user) {
        validator.validateUserForCreate(user);

        if (userRepository.isEmailBooked(user)) {
            throw new UserEmailIsAlreadyExists("Пользователь с данной почтой уже зарегистрирован в системе");
        }

        return userRepository.createUser(user);
    }

    @Override
    public User patchUser(long userId, User userUpdates) {

        validator.validateUserForUpdate(userUpdates);

        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + userId + " не существует"));
        String newName = userUpdates.getName();
        if (newName != null) {
            user.setName(newName);
        }

        String newEmail = userUpdates.getEmail();
        if (newEmail != null) {
            if (!newEmail.equals(user.getEmail())) {
                if (userRepository.isEmailBooked(userUpdates)) {
                    throw new UserEmailIsAlreadyExists("Пользователь с данной почтой уже зарегистрирован в системе");
                }
                user.setEmail(newEmail);
            }
        }

        userRepository.updateUser(user);
        return user;

    }

    @Override
    public User getUserById(long id) {
        if (id < 0) {
            throw new UserValidateException("id пользователя не может быть отрицательным");
        }

        return userRepository.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + id + " не существует"));
    }

    @Override
    public List<User> getUsers() {
        return userRepository.getUsers();
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
