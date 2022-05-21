package ru.duzhinsky.preorderbot.bot.handlers;

import ru.duzhinsky.preorderbot.bot.TelegramBot;

public abstract class TelegramChatHandler {
    protected TelegramBot bot;
    protected Short stateOrdinal;

    public TelegramChatHandler(TelegramBot bot, Short stateOrdinal) {
        this.bot = bot;
        this.stateOrdinal = stateOrdinal;
    }

    public void handle(ChatUpdate<?> upd) {
        init();
        handleAction(upd);
        close();
    }

    public abstract void init();
    public abstract void handleAction(ChatUpdate<?> upd);
    public abstract void close();
}
