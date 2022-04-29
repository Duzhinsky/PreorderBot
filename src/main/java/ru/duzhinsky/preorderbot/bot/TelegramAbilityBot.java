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
import ru.duzhinsky.preorderbot.db.MySQLDAOFactory;
import ru.duzhinsky.preorderbot.db.UserDao;
import ru.duzhinsky.preorderbot.utils.PhoneValidator;

import java.sql.SQLException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

public class TelegramAbilityBot extends AbilityBot {
    private static final String BOT_TOKEN;
    private static final String BOT_USERNAME;
    private static final long   CREATOR_ID;
    private static final BareboneToggle toggle = new BareboneToggle();
    private Map<Long, Boolean> isUserRegistered;

    private final UserDao userDao;

    static {
        BOT_TOKEN = Config.getProperty("token","");
        BOT_USERNAME = Config.getProperty("username","");
        CREATOR_ID = Integer.parseInt(Config.getProperty("creatorId","0"));
    }

    @Override
    public long creatorId() {
        return CREATOR_ID;
    }

    public TelegramAbilityBot() throws SQLException {
        super(BOT_TOKEN, BOT_USERNAME, toggle);
        userDao = MySQLDAOFactory.getUserDao();
        isUserRegistered = db.<Long, Boolean>getMap("isUserRegistered");
    }

    public ReplyFlow start() {
        return ReplyFlow.builder(db)
                .onlyIf(Flag.MESSAGE)
                .onlyIf(upd->"/start".equals(upd.getMessage().getText()))
                .action((bot,upd) -> {
                    silent.send("Приветствие в начале", getChatId(upd));
                    isUserRegistered.put(
                            getChatId(upd),
                            userDao.isUserPresentByTgUsername(upd.getMessage().getChat().getUserName())
                    );
                })
                .next(authFlow())
                .next(mainFlow())
                .build();
    }

    private ReplyFlow mainFlow() {
        return ReplyFlow.builder(db)
                .onlyIf(isUserRegisteredByUpdate())
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


    private ReplyFlow authFlow() {
        return ReplyFlow.builder(db)
                .onlyIf(isUserRegisteredByUpdate().negate())
                .action(getAuthAction())
                .next(loginButtonReply())
                .next(registerButtonReply())
                .build();
    }

    private ReplyFlow loginButtonReply() {
        BiConsumer<BaseAbilityBot, Update> action = (bot, upd) -> {
            Long chatId = getChatId(upd);
            Integer sourceMessage = getSourceMessageId(upd);
            deleteMessage(sourceMessage, chatId);
            silent.send("login", chatId);
        };
        return ReplyFlow.builder(db)
                .onlyIf(Flag.CALLBACK_QUERY)
                .onlyIf(upd -> upd.getCallbackQuery().getData().equals("LOGIN_BUTTON"))
                .action(action)
                .build();
    }

    private ReplyFlow registerButtonReply() {
        BiConsumer<BaseAbilityBot, Update> action = (bot, upd) -> {
            Long chatId = getChatId(upd);
            Integer sourceMessage = getSourceMessageId(upd);
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
//                    getAuthAction().accept(bot,upd);
                })
                .onlyIf(Flag.CALLBACK_QUERY)
                .onlyIf(upd -> upd.getCallbackQuery().getData().equals("BACK_TO_AUTH"))
                .next(authFlow())
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

    private Integer getSourceMessageId(Update upd) {
        return upd.hasMessage() ?
                upd.getMessage().getMessageId() : (
                upd.hasCallbackQuery() ?
                        upd.getCallbackQuery().getMessage().getMessageId() : 0
        );
    }

    private Predicate<Update> isUserRegisteredByUpdate() {
        return upd -> isUserRegistered.get(getChatId(upd));
    }
}
