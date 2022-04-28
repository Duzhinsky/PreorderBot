package ru.duzhinsky.preorderbot.bot;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.toggle.BareboneToggle;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.duzhinsky.preorderbot.data.Config;
import ru.duzhinsky.preorderbot.db.UserDB;
import ru.duzhinsky.preorderbot.db.UserDao;
import ru.duzhinsky.preorderbot.objects.User;
import ru.duzhinsky.preorderbot.utils.PhoneValidator;

import java.util.function.BiConsumer;
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

    @Override
    public long creatorId() {
        return CREATOR_ID;
    }

    public TelegramAbilityBot() {
        super(BOT_TOKEN, BOT_USERNAME, toggle);
    }

    public ReplyFlow start() {
        return ReplyFlow.builder(db)
                .action((bot,upd) -> silent.send("Приветствие в начале", getChatId(upd)))
                .onlyIf(Flag.MESSAGE)
                .onlyIf(upd->upd.getMessage().getText().equals("/start"))
                .next(auth())
//                .next(Reply.of((bot,upd) -> silent.send("helo", upd.getMessage().getChatId())))
                .build();
    }

    private ReplyFlow auth() {
        return ReplyFlow.builder(db)
                .action(getAuthAction())
                .onlyIf(Flag.MESSAGE)
                .onlyIf(upd -> !isUserPresent(upd.getMessage().getChat().getUserName()))
                .build();
    }

    private BiConsumer<BaseAbilityBot, Update> getAuthAction() {
        return (bot, upd) -> {
            Long chatId = getChatId(upd);
            SendMessage sendKeyboard = new SendMessage();
            sendKeyboard.setText("Похоже, вы используете телеграм бот для заказа впервые\nЕсли вы уже пользовались нашими сервисами, войдите по номеру телефона");
            sendKeyboard.setChatId(chatId.toString());
            sendKeyboard.setReplyMarkup(TelegramKeyboards.loginOrRegisterKeyboard());
            try {
                execute(sendKeyboard);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
    }

    private boolean isUserPresent(String username) {
        UserDao userDao = new UserDB();
        var userOpt = userDao.getUser(User.fromTgUsername(username));
        return userOpt.isPresent();
    }

    public ReplyFlow loginFlow() {
        BiConsumer<BaseAbilityBot, Update> action = (bot, upd) -> {
            Long chatId = getChatId(upd);
            silent.send("login", chatId);
        };
        return ReplyFlow.builder(db)
                .onlyIf(Flag.CALLBACK_QUERY)
                .onlyIf(upd -> upd.getCallbackQuery().getData().equals("LOGIN_BUTTON"))
                .action(action)
                .build();
    }

    public ReplyFlow registerFlow() {
        BiConsumer<BaseAbilityBot, Update> action = (bot, upd) -> {
            Long chatId = getChatId(upd);
            Integer sourceMessage = upd.hasMessage() ?
                    upd.getMessage().getMessageId() : (
                            upd.hasCallbackQuery() ?
                                    upd.getCallbackQuery().getMessage().getMessageId() : 0
                    );
            deleteMessage(sourceMessage, chatId);
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText("Введите номер телефона, который будет использоваться для входа");
            message.setReplyMarkup(TelegramKeyboards.backButton("BACK_TO_AUTH"));
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
        return ReplyFlow.builder(db)
                .onlyIf(Update::hasCallbackQuery)
                .onlyIf(upd -> upd.getCallbackQuery().getData().equals("REGISTER_BUTTON"))
                .action(action)
                .next(validatePhone())
                .build();

    }

    private ReplyFlow validatePhone() {
        BiConsumer<BaseAbilityBot, Update> action = (bot,upd) -> {

        };
        return ReplyFlow.builder(db)
                .onlyIf(Flag.MESSAGE)
                .onlyIf(upd -> {
                    String input = upd.getMessage().getText();
                    if(PhoneValidator.validate(PhoneValidator.prepare(input)))
                        return true;
                    silent.send("Неправильный формат телефона, попробуйте еще", getChatId(upd));
                    return false;
                })
                .action(action)
                .build();
    }

    public ReplyFlow backToAuth() {
        return ReplyFlow.builder(db)
                .action((bot,upd) -> {
                    deleteMessage(upd.getCallbackQuery().getMessage().getMessageId(), getChatId(upd));
                    getAuthAction().accept(bot,upd);
                })
                .onlyIf(Flag.CALLBACK_QUERY)
                .onlyIf(upd -> upd.getCallbackQuery().getData().equals("BACK_TO_AUTH"))
                .build();
    }

    private void deleteMessage(Integer messageId, Long chatId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId.toString());
        deleteMessage.setMessageId(messageId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
