package ru.duzhinsky.preorderbot.service.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;

public interface UpdateHandler {
    void handle(TgChat chat, Update update);
    ChatState getHandlerScope();
}
