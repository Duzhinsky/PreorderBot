package ru.duzhinsky.preorderbot.service.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {
    void handle(Update update);
    ChatState getHandlerScope();
}
