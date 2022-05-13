package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;

import java.util.Optional;

public interface TelegramDao {
    void associateUserWithTelegram(String phoneNumber, Long chatId) throws Exception;
    Optional<User> getUserByTgChatId(Long chatId) throws Exception;
    Optional<String> getLastVerifiedPhone(Long chatId) throws Exception;
    void logMessage(Long chatId, Integer messageId) throws Exception;
    void clearMessagePool(Long charId) throws Exception;
}
