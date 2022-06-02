package ru.duzhinsky.preorderbot.bot.handlers;

import ru.duzhinsky.preorderbot.bot.TelegramBot;
import ru.duzhinsky.preorderbot.bot.updates.ChatUpdate;

public class RegistrationChatHandler extends TelegramChatHandler {
    private enum State {

    }
    private State state;

    public RegistrationChatHandler(TelegramBot bot, Short stateOrdinal) {
        super(bot, stateOrdinal);
    }

    @Override
    public void init() {

    }

    @Override
    public void close() {

    }

    @Override
    public void handleAction(ChatUpdate<?> upd) {

    }
}
