package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.UserEmailIsAlreadyExists;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, String> emails = new HashMap<>();
    private static long id;

    private long increaseId() {
        return ++id;
    }

    @Override
    public User createUser(User user) {
        if (isEmailBooked(user)) {
            throw new UserEmailIsAlreadyExists("Данная почта уже занята");
        }
        user.setId(increaseId());
        long userId = user.getId();
        users.put(userId, user);
        emails.put(userId, user.getEmail());
        return user;
    }

    @Override
    public void updateUser(User user) {
        if (isEmailBooked(user)) {
            throw new UserEmailIsAlreadyExists("Данная почта уже занята");
        }
        long userId = user.getId();
        users.put(userId, user);
        emails.put(userId, user.getEmail());
    }

    @Override
    public Optional<User> getUserById(long id) {
        User user = users.get(id);
        if (user != null) {
            user.setEmail(emails.get(id));
        }
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(long id) {
        User user = users.get(id);
        emails.remove(id);
        users.remove(id);
    }

    @Override
    public void deleteAllUsers() {
        emails.clear();
        users.clear();
    }

    private boolean isEmailBooked(User user) {
        String userEmail = user.getEmail();
        if (!emails.containsValue(userEmail)) {
            return false;
        }
        Long userId = user.getId();
        if (userId == null) {
            return true;
        }

        if (emails.get(userId).equals(userEmail)) {
            return false;
        }

        return true;
    }
}
