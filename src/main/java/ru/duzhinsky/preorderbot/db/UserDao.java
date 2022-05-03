package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;
import java.util.Optional;

public interface UserDao {
    void addUser(String phoneNumber) throws Exception;
    void associateUserWithTelegram(String phoneNumber, Long chatId) throws Exception;
    Optional<User> getUserByTgChatId(Long chatId) throws Exception;
    Optional<User> getUserByPhone(String phoneNumber) throws Exception;
    boolean isUserPresentByTgChatId(Long chatId) throws Exception;
}
