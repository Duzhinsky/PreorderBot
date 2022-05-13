package ru.duzhinsky.preorderbot.bot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramChatHandler {
    void handle(Update upd);
}
