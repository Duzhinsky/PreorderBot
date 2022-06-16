package ru.duzhinsky.preorderbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.duzhinsky.preorderbot.bot.PreorderBot;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;
import ru.duzhinsky.preorderbot.service.handlers.HandlersContext;

@Service
@Scope("singleton")
public class RedirectionReceiver implements Runnable{
    private final PreorderBot bot;
    private final HandlersContext handlersContext;

    @Autowired
    public RedirectionReceiver(PreorderBot bot, HandlersContext handlersContext) {
        this.bot = bot;
        this.handlersContext = handlersContext;
    }

    @Override
    public void run() {
        while(true) {
            for (TgChat chat = bot.getRedirectionQueue().poll(); chat != null; chat = bot.getRedirectionQueue().poll()) {
                redirect(chat);
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

    private void redirect(TgChat chat) {
        handlersContext.handleUpdate(chat, null);
    }
}
