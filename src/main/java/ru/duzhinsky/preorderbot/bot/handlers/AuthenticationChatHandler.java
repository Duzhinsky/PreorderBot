package ru.duzhinsky.preorderbot.bot.handlers;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.duzhinsky.preorderbot.bot.TelegramBot;
import ru.duzhinsky.preorderbot.entities.TgChat;
import ru.duzhinsky.preorderbot.entities.dao.DAOFactory;
import ru.duzhinsky.preorderbot.entities.dao.TgChatDAO;

import java.util.List;

public class AuthenticationChatHandler implements TelegramChatHandler {
    private enum State {
        SEND_MESSAGE,
        WAITING_KEYBOARD_REPLY
    }
    private final State state;
    private TgChat chat;

    private final TelegramBot bot;
    private final TgChatDAO chatRepository;

    public AuthenticationChatHandler(TelegramBot bot, Short stateOrdinal) {
        this.bot = bot;
        this.chatRepository = DAOFactory.getTgChatDAO();

        if(stateOrdinal == null) stateOrdinal = 0;
        state = State.values()[stateOrdinal];
    }

    @Override
    public void handle(Update upd) {
        chat = chatRepository.findById(getChatId(upd));
        if(state == State.SEND_MESSAGE) {
            sendMessage(upd);
            chat.setChatHandlerState((short)State.WAITING_KEYBOARD_REPLY.ordinal());
        } else if(state == State.WAITING_KEYBOARD_REPLY) {
            if(!upd.hasCallbackQuery()) return;
            handleKeyboard(upd);
        }
        chatRepository.persist(chat);
    }

    private void handleKeyboard(Update upd) {
        String option = upd.getCallbackQuery().getData();
        if("LOGIN_BUTTON".equals(option)) {
            chat.setChatState(ChatState.LOGIN);
            chat.setChatHandlerState((short)0);
        } else if("REGISTER_BUTTON".equals(option)) {
            chat.setChatState(ChatState.REGISTRATION);
            chat.setChatHandlerState((short)0);
        }
    }

    private void sendMessage(Update upd) {
        Long chatId = getChatId(upd);
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText(
                "Похоже, вы используете телеграм бот для заказа впервые\n" +
                        "Если вы уже пользовались нашими сервисами, войдите по номеру телефона"
        );
        msg.setReplyMarkup(loginOrRegisterKeyboard());
        bot.getSendQueue().add(msg);
    }

    private static InlineKeyboardMarkup loginOrRegisterKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton loginButton = new InlineKeyboardButton();
        loginButton.setText("Войти");
        loginButton.setCallbackData("LOGIN_BUTTON");
        InlineKeyboardButton registerButton = new InlineKeyboardButton();
        registerButton.setText("Регистрация");
        registerButton.setCallbackData("REGISTER_BUTTON");
        markup.setKeyboard(List.of(List.of(loginButton), List.of(registerButton)));
        return markup;
    }
}
