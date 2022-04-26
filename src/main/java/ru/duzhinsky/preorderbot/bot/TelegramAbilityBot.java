package ru.duzhinsky.preorderbot.bot;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.abilitybots.api.toggle.BareboneToggle;
import ru.duzhinsky.preorderbot.data.Config;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

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

    public Ability start() {
        // TODO add start action (register/login user)
        return Ability.builder()
                .name("start")
                .input(0)
                .locality(USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> silent.send("Hello there!", ctx.chatId()))
                .build();
    }

    @Override
    public long creatorId() {
        return CREATOR_ID;
    }
}
