package ru.duzhinsky.preorderbot.service.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;

import java.util.List;

public interface UpdateHandler {
    void handle(TgChat chat, Update update);
    List<ChatState> getHandlerScope();
}
