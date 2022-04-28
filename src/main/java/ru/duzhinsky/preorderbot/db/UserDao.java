package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;
import java.util.Optional;

public interface UserDao {
    void addUser(User user);
    Optional<User> getUserByTgUsername(String username);
}
