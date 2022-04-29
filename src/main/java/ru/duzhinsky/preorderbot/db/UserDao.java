package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;
import java.util.Optional;

public interface UserDao {
    void addUser(String phoneNumber);
    void associateUserWithTelegram(String phoneNumber, String tgUsername);
    Optional<User> getUserByTgUsername(String username);
    Optional<User> getUserByPhone(String phoneNumber);
    boolean isUserPresentByTgUsername(String username);
}
