package ru.duzhinsky.preorderbot.bot.handlers;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.duzhinsky.preorderbot.bot.TelegramBot;
import ru.duzhinsky.preorderbot.persistence.entities.TgChat;
import ru.duzhinsky.preorderbot.persistence.entities.dao.DAOFactory;
import ru.duzhinsky.preorderbot.persistence.entities.dao.TgChatDAO;

import java.util.List;

public class AuthenticationChatHandler implements TelegramChatHandler {
    private enum State {
        SEND_MESSAGE,
        WAITING_KEYBOARD_REPLY
    }
    private final State state;
    private TgChat chat;

    private final TelegramBot bot;
    private final TgChatDAO chatDAO;

    public AuthenticationChatHandler(TelegramBot bot, Short stateOrdinal) {
        this.bot = bot;
        this.chatDAO = DAOFactory.getTgChatDAO();

        if(stateOrdinal == null) stateOrdinal = 0;
        state = State.values()[stateOrdinal];
    }

    @Override
    public void handle(Update upd) {
        chat = chatDAO.findById(getChatId(upd));
        if(state == State.SEND_MESSAGE) {
            sendMessage(upd);
            chatDAO.update(
                    chat.getId(),
                    c -> c.setChatHandlerState((short)State.WAITING_KEYBOARD_REPLY.ordinal())
            );
        } else if(state == State.WAITING_KEYBOARD_REPLY) {
            if(!upd.hasCallbackQuery()) return;
            handleKeyboard(upd);
        }
        chatDAO.close();
    }

    private void handleKeyboard(Update upd) {
        String option = upd.getCallbackQuery().getData();
        if("LOGIN_BUTTON".equals(option)) {
            chatDAO.update(
                    chat.getId(),
                    c -> c.setChatState(ChatState.LOGIN),
                    c -> c.setChatHandlerState((short)0)
            );
            bot.getReceiveQueue().add(upd);
        } else if("REGISTER_BUTTON".equals(option)) {
            chatDAO.update(
                    chat.getId(),
                    c -> c.setChatState(ChatState.REGISTRATION),
                    c -> c.setChatHandlerState((short)0)
            );
            bot.getReceiveQueue().add(upd);
        }
    }

    private void sendMessage(Update upd) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chat.getId().toString());
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
        markup.setKeyboard(List.of(List.of(loginButton, registerButton)));
        return markup;
    }
}
