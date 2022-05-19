package ru.duzhinsky.preorderbot.bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramUpdatesSender implements Runnable {
    private final TelegramBot bot;

    public TelegramUpdatesSender(TelegramBot bot) {
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
