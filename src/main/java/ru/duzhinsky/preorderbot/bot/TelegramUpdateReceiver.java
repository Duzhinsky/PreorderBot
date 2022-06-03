package ru.duzhinsky.preorderbot.bot;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChatRepository;

@Log
@Service
@Scope("singleton")
public class TelegramUpdateReceiver implements Runnable {
    private final TelegramBot bot;
    private final TgChatRepository tgChatRepository;

    @Autowired
    public TelegramUpdateReceiver(TelegramBot bot, TgChatRepository tgChatRepository) {
        this.bot = bot;
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
        if(update.hasCallbackQuery()) {

        }

        if(update.hasMessage()) {

        }
    }
}
