package ru.duzhinsky.preorderbot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Scope("singleton")
public class UpdatesSender extends Thread {
    private final PreorderBot bot;

    @Autowired
    public UpdatesSender(PreorderBot bot) {
        super("UpdatesSender");
        this.bot = bot;
    }

    @Override
    public void run() {
        while (true) {
            for (Object object = bot.getSendQueue().poll(); object != null; object = bot.getSendQueue().poll()) {
                send(object);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void send(Object object) {
        if(object instanceof BotApiMethod) {
            try {
                bot.execute((BotApiMethod) object);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
