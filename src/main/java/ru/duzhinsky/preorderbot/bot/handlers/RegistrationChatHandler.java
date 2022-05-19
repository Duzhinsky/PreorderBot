package ru.duzhinsky.preorderbot.bot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.TelegramBot;

public class RegistrationChatHandler implements TelegramChatHandler {
    private enum State {

    }
    private final State state;
    private final TelegramBot bot;

    public RegistrationChatHandler(TelegramBot bot, Short stateOrdinal) {
        this.bot = bot;
        this.state = State.values()[stateOrdinal];
    }

    @Override
    public void handle(Update upd) {

    }
}
