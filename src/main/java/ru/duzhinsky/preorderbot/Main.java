package ru.duzhinsky.preorderbot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.duzhinsky.preorderbot.bot.TelegramBot;
import ru.duzhinsky.preorderbot.persistence.EntityManagerUtil;


public class Main {
    public static void main(String[] args) {
        try {
            Class.forName(EntityManagerUtil.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TelegramBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
