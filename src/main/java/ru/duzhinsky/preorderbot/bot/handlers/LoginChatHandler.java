package ru.duzhinsky.preorderbot.bot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.TelegramBot;

public class LoginChatHandler implements TelegramChatHandler {
    private enum State {
        REQUEST_PHONE,
    }
    private final State state;
    private final TelegramBot bot;

    public LoginChatHandler(TelegramBot bot, Short stateOrdinal) {
        this.bot = bot;
        this.state = State.values()[stateOrdinal];
    }

    @Override
    public void handle(Update upd) {

    }
}
