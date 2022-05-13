package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;
import java.util.Optional;

public interface UserDao {
    void addUser(String phoneNumber) throws Exception;
    Optional<User> getUserByPhone(String phoneNumber) throws Exception;
}
