package ru.duzhinsky.preorderbot.service.handlers.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.PreorderBot;
import ru.duzhinsky.preorderbot.service.handlers.ChatState;
import ru.duzhinsky.preorderbot.service.handlers.UpdateHandler;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChatRepository;

import java.util.List;

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
    public void handle(TgChat chat, Update update) {
        if(chat.getCustomer() == null)
            chat.setChatState(ChatState.AUTHENTICATION);
        else
            chat.setChatState(ChatState.MAIN_MENU);
        tgChatRepository.save(chat);
        preorderBot.getRedirectionQueue().add(chat);
    }

    @Override
    public List<ChatState> getHandlerScope() {
        return List.of(
                ChatState.DEFAULT
        );
    }
}
