package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;
import java.util.Optional;

public interface UserDao {
    void addUser(String phoneNumber) throws Exception;
    void associateUserWithTelegram(String phoneNumber, String tgUsername) throws Exception;
    Optional<User> getUserByTgUsername(String username) throws Exception;
    Optional<User> getUserByPhone(String phoneNumber) throws Exception;
    boolean isUserPresentByTgUsername(String username) throws Exception;
}
