package ru.duzhinsky.preorderbot.bot.updates;

import ru.duzhinsky.preorderbot.bot.TelegramBot;

public class EmptyUpdate extends ChatUpdate<Object> {
    public EmptyUpdate(TelegramBot bot, Long chatId) {
        super(bot, chatId, null);
    }
}
