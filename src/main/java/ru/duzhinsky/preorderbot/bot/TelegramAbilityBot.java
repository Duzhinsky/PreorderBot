package ru.duzhinsky.preorderbot.bot;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.toggle.BareboneToggle;

public class TelegramAbilityBot extends AbilityBot {
    private static final String BOT_TOKEN = "";
    private static final String BOT_USERNAME = "";
    private static final BareboneToggle toggle = new BareboneToggle();

    static {

    }

    public TelegramAbilityBot() {
        super(BOT_TOKEN, BOT_USERNAME, toggle);
    }

    @Override
    public long creatorId() {
        return 0;
    }
}
