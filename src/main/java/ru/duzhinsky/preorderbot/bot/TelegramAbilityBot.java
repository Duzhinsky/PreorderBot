package ru.duzhinsky.preorderbot.bot;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.toggle.BareboneToggle;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.duzhinsky.preorderbot.data.Config;
import ru.duzhinsky.preorderbot.db.UserDB;
import ru.duzhinsky.preorderbot.db.UserDao;
import ru.duzhinsky.preorderbot.objects.User;

import java.util.List;
import java.util.function.BiConsumer;

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

    @Override
    public long creatorId() {
        return CREATOR_ID;
    }

    public TelegramAbilityBot() {
        super(BOT_TOKEN, BOT_USERNAME, toggle);
    }

    public ReplyFlow start() {
        return ReplyFlow.builder(db)
                .action((bot,upd) -> silent.send("Приветствие в начале", upd.getMessage().getChatId()))
                .onlyIf(upd->upd.getMessage().getText().equals("/start"))
                .next(auth())
                .next(Reply.of((bot,upd) -> silent.send("helo", upd.getMessage().getChatId())))
                .build();
    }

    private Reply auth() {
        BiConsumer<BaseAbilityBot, Update> reply = (bot,upd) -> {
            long chatId = upd.getMessage().getChatId();
            silent.send("Похоже, вы используете телеграм бот для заказа впервые", chatId);
            SendMessage sendKeyboard = new SendMessage();
            sendKeyboard.setText("Если вы уже пользовались нашими сервисами, войдите по номеру телефона");
            sendKeyboard.setChatId(String.valueOf(chatId));
            sendKeyboard.setReplyMarkup(TelegramKeyboards.loginOrRegisterKeyboard());
            try {
                execute(sendKeyboard);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
        return Reply.of(reply, upd -> {
            try(UserDao userDao = new UserDB()) {
                String username = upd.getMessage().getChat().getUserName();
                var userOpt = userDao.getUser(User.fromTgUsername(username));
                if(userOpt.isPresent()) return false;
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
            return true;
        });
    }

    public Reply loginReply() {
        BiConsumer<BaseAbilityBot, Update> reply = (bot, upd) -> {
            silent.send("введите номер телефона", upd.getMessage().getChatId());
            SendMessage msg = new SendMessage();
        };
        return Reply.of(
                reply,
                Flag.CALLBACK_QUERY,
                upd -> upd.getCallbackQuery().getData().equals("LOGIN_BUTTON")
        );
    }

    public Reply registerReply() {
        BiConsumer<BaseAbilityBot, Update> reply = (bot, upd) -> {

        };
        return Reply.of(
                reply,
                Flag.CALLBACK_QUERY,
                upd -> upd.getCallbackQuery().getData().equals("REGISTER_BUTTON")
        );
    }
}
