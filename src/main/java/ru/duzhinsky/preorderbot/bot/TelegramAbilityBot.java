package ru.duzhinsky.preorderbot.bot;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.toggle.BareboneToggle;
import ru.duzhinsky.preorderbot.data.Config;

public class TelegramAbilityBot extends AbilityBot {
    private static final String BOT_TOKEN;
    private static final String BOT_USERNAME;
    private static final long   CREATOR_ID;
    private static final BareboneToggle toggle = new BareboneToggle();

    static {
        BOT_TOKEN = Config.getProperty("token","");
        BOT_USERNAME = Config.getProperty("username","");
        CREATOR_ID = Integer.parseInt(Config.getProperty("creatorId","0"));
    }

    public TelegramAbilityBot() {
        super(BOT_TOKEN, BOT_USERNAME, toggle);
    }

    @Override
    public long creatorId() {
        return CREATOR_ID;
    }
}
