package ru.duzhinsky.preorderbot.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.data.Config;

public class TelegramBot extends TelegramLongPollingBot {
    private static final String BOT_TOKEN;
    private static final String BOT_USERNAME;

    static {
        BOT_TOKEN = Config.getProperty("token","");
        BOT_USERNAME = Config.getProperty("username","");
    }

    public TelegramBot() {
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

    }
}
