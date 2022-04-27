package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;
import java.util.Optional;

public interface UserDao extends AutoCloseable {
    void addUser(User user);
    Optional<User> getUser(User user);
}
