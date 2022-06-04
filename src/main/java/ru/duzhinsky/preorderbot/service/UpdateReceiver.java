package ru.duzhinsky.preorderbot.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.PreorderBot;
import ru.duzhinsky.preorderbot.bot.TelegramUtils;
import ru.duzhinsky.preorderbot.service.handlers.ChatState;
import ru.duzhinsky.preorderbot.service.handlers.HandlersContext;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChatRepository;

@Log
@Service
@Scope("singleton")
public class UpdateReceiver implements Runnable {
    private final PreorderBot bot;
    private final HandlersContext handlersContext;
    private final TgChatRepository tgChatRepository;

    @Autowired
    public UpdateReceiver(PreorderBot bot, HandlersContext handlersContext, TgChatRepository tgChatRepository) {
        this.bot = bot;
        this.handlersContext = handlersContext;
        this.tgChatRepository = tgChatRepository;
    }

    @Override
    public void run() {
        while(true) {
            for (Update upd = bot.getReceiveQueue().poll(); upd != null; upd = bot.getReceiveQueue().poll()) {
                handleUpdate(upd);
            }
            try {
                int WAIT_FOR_NEW_MESSAGE_DELAY = 100;
                Thread.sleep(WAIT_FOR_NEW_MESSAGE_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void handleUpdate(Update update) {
        log.info("update received");
        Long chatId = TelegramUtils.getChatId(update);
        tgChatRepository.findById(chatId).ifPresentOrElse(
                chat -> {
                    handlersContext.handleUpdate(chat, update);
                },
                () -> {
                    TgChat newChat = new TgChat();
                    newChat.setId(chatId);
                    newChat.setChatState(ChatState.DEFAULT);
                    tgChatRepository.save(newChat);
                    handlersContext.handleUpdate(newChat, update);
                }
        );
    }
}
