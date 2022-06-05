package ru.duzhinsky.preorderbot.service.handlers.impl;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;
import ru.duzhinsky.preorderbot.service.handlers.ChatState;
import ru.duzhinsky.preorderbot.service.handlers.UpdateHandler;

public class RegistrationHandler implements UpdateHandler {


    @Override
    public void handle(TgChat chat, Update update) {

    }

    @Override
    public ChatState getHandlerScope() {
        return ChatState.REGISTRATION;
    }
}
