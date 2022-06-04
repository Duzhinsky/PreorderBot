package ru.duzhinsky.preorderbot.service.handlers.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.PreorderBot;
import ru.duzhinsky.preorderbot.bot.TelegramUtils;
import ru.duzhinsky.preorderbot.service.handlers.ChatState;
import ru.duzhinsky.preorderbot.service.handlers.UpdateHandler;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChatRepository;

@Service
public class DefaultHandler implements UpdateHandler {
    private final TgChatRepository tgChatRepository;
    private final PreorderBot preorderBot;

    @Autowired
    public DefaultHandler(TgChatRepository tgChatRepository, PreorderBot preorderBot) {
        this.tgChatRepository = tgChatRepository;
        this.preorderBot = preorderBot;
    }

    @Override
    public void handle(Update update) {
        TgChat chat = tgChatRepository.findById(TelegramUtils.getChatId(update)).get();
        if(chat.getCustomer() == null)
            chat.setChatState(ChatState.AUTHENTICATION);
        else
            chat.setChatState(ChatState.MAIN_MENU);
        tgChatRepository.save(chat);
        preorderBot.getReceiveQueue().add(update);
    }

    @Override
    public ChatState getHandlerScope() {
        return ChatState.DEFAULT;
    }
}
