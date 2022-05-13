package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.bot.handlers.TelegramChatHandlerEnum;
import ru.duzhinsky.preorderbot.objects.User;

import java.util.Optional;

public interface TelegramDao {
    TelegramChatHandlerEnum getChatHandler(Long  chatId);
    void associateUserWithTelegram(String phoneNumber, Long chatId) throws Exception;
    Optional<User> getUserByTgChatId(Long chatId) throws Exception;
    Optional<String> getLastVerifiedPhone(Long chatId) throws Exception;
}
