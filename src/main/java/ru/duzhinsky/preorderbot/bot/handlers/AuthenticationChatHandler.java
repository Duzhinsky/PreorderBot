package ru.duzhinsky.preorderbot.bot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.duzhinsky.preorderbot.bot.TelegramBot;
import ru.duzhinsky.preorderbot.bot.updates.ChatUpdate;
import ru.duzhinsky.preorderbot.persistence.entities.TgChat;
import ru.duzhinsky.preorderbot.persistence.dao.EntityDao;
import ru.duzhinsky.preorderbot.persistence.dao.JpaDaoFactory;

import java.util.List;

public class AuthenticationChatHandler extends TelegramChatHandler {
    private static final String messageText =
            "Похоже, вы используете телеграм бот для заказа впервые\n" +
            "Если вы уже пользовались нашими сервисами, войдите по номеру телефона";

    private enum State {
        SEND_MESSAGE,
        WAITING_KEYBOARD_REPLY
    }
    private State state;
    private TgChat chat;
    private EntityDao<TgChat, Long> chatDAO;

    public AuthenticationChatHandler(TelegramBot bot, Short stateOrdinal) {
        super(bot, stateOrdinal);
    }

    @Override
    public void init() {
        this.chatDAO = new JpaDaoFactory<TgChat, Long>().getDao(TgChat.class);
        if(stateOrdinal == null)
            stateOrdinal = 0;
        if(stateOrdinal < State.values().length)
            state = State.values()[stateOrdinal];
    }

    @Override
    public void close() {
        chatDAO.close();
    }

    @Override
    public void handleAction(ChatUpdate<?> upd) {
        chat = chatDAO.find(upd.getChatId());
        if(state == State.SEND_MESSAGE) {
            sendMessage(upd);
            chatDAO.update(
                    chat,
                    c -> c.setChatHandlerState((short)State.WAITING_KEYBOARD_REPLY.ordinal())
            );
        } else if(state == State.WAITING_KEYBOARD_REPLY) {
            handleKeyboard(upd);
        }
    }

    private void handleKeyboard(ChatUpdate<?> upd) {
        if(!(upd.getContent() instanceof Update)) return;
        Update update = (Update)upd.getContent();
        if(!update.hasCallbackQuery()) return;
        String option = update.getCallbackQuery().getData();
        if("LOGIN_BUTTON".equals(option)) {
            chatDAO.update(
                    chat,
                    c -> c.setChatState(ChatState.LOGIN),
                    c -> c.setChatHandlerState((short)0)
            );
            bot.getReceiveQueue().add(upd);
        } else if("REGISTER_BUTTON".equals(option)) {
            chatDAO.update(
                    chat,
                    c -> c.setChatState(ChatState.REGISTRATION),
                    c -> c.setChatHandlerState((short)0)
            );
            bot.getReceiveQueue().add(upd);
        }
    }

    private void sendMessage(ChatUpdate<?> upd) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chat.getId().toString());
        msg.setText(messageText);
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
