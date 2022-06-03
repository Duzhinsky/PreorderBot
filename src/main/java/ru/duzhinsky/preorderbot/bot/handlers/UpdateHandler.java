package ru.duzhinsky.preorderbot.bot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface UpdateHandler {
    SendMessage handle(Message message);
    ChatState getHandlerName();
}
