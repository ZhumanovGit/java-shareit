package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.UserValidateException;

@Service
public class UserValidator {

    public void validateUserForCreate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new UserValidateException("Имя не может быть пустым");
        }

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new UserValidateException("Некорректные данные почты");
        }
    }

    public void validateUserForUpdate(User user) {

        if (user.getName() != null && user.getName().isBlank()) {
            throw new UserValidateException("Имя не может быть пустым");
        }

        if (user.getEmail() != null) {
            if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
                throw new UserValidateException("Некорректные данные почты");
            }
        }

    }
}
