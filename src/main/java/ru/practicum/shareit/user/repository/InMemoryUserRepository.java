package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.UserEmailIsAlreadyExists;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private static long id;

    private long increaseId() {
        return ++id;
    }

    @Override
    public User createUser(User user) {
        String userEmail = user.getEmail();
        if (emails.contains(userEmail)) {
            throw new UserEmailIsAlreadyExists("Данная почта уже занята");
        }
        user.setId(increaseId());
        long userId = user.getId();
        users.put(userId, user);
        emails.add(userEmail);
        return user;
    }

    @Override
    public void updateUser(User user) {
        long userId = user.getId();
        String userEmail = user.getEmail();
        String oldUserEmail = users.get(userId).getEmail();
        if (!oldUserEmail.equals(userEmail) && (emails.contains(userEmail))) {
            throw new UserEmailIsAlreadyExists("Данная почта уже занята");
        }
        emails.remove(oldUserEmail);
        users.put(userId, user);
        emails.add(userEmail);
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(long id) {
        User user = users.remove(id);
        if (user != null) {
            emails.remove(user.getEmail());
        }
    }

    @Override
    public void deleteAllUsers() {
        emails.clear();
        users.clear();
    }
}
