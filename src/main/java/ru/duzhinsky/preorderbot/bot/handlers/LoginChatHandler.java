package ru.duzhinsky.preorderbot.bot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.duzhinsky.preorderbot.bot.TelegramBot;
import ru.duzhinsky.preorderbot.persistence.entities.TgChat;
import ru.duzhinsky.preorderbot.persistence.dao.EntityDAO;
import ru.duzhinsky.preorderbot.persistence.dao.JPADAOFactory;

import java.util.List;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

public class LoginChatHandler implements TelegramChatHandler {
    private static final String requestPhoneText =
            "Для входа введите номер телефона";

    private enum State {
        REQUEST_PHONE,
        INPUT_PHONE,
        VERIFY_PHONE
    }
    private final State state;
    private TgChat chat;

    private final TelegramBot bot;
    private final EntityDAO<TgChat, Long> chatDAO;

    public LoginChatHandler(TelegramBot bot, Short stateOrdinal) {
        this.bot = bot;
        this.state = State.values()[stateOrdinal];
        this.chatDAO = new JPADAOFactory().getDao(TgChat.class);
    }

    @Override
    public void handle(Update upd) {
        chat = chatDAO.find(getChatId(upd));
        if(state == State.REQUEST_PHONE) {
            requestPhone(upd);
            chatDAO.update(
                    chat,
                    c -> c.setChatHandlerState((short)State.INPUT_PHONE.ordinal())
            );
            bot.getReceiveQueue().add(upd);
        } else if(state == State.INPUT_PHONE) {
            if(upd.hasCallbackQuery()) {
                String option = upd.getCallbackQuery().getData();
                if("BACK_TO_AUTH".equals(option)) {
                    chatDAO.update(
                            chat,
                            c -> c.setChatState(ChatState.AUTHENTICATION),
                            c -> c.setChatHandlerState((short)0)
                    );
                    bot.getReceiveQueue().add(upd);
                }
            }
            if(upd.hasMessage()) 
                checkPhone(upd);
        }
        chatDAO.close();
    }

    private void checkPhone(Update upd) {
    }

    private void requestPhone(Update upd) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chat.getId().toString());
        msg.setText(requestPhoneText);
        msg.setReplyMarkup(getBackKeyboard("BACK_TO_AUTH"));
        bot.getSendQueue().add(msg);
    }

    private ReplyKeyboard getBackKeyboard(String data) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData(data);
        markup.setKeyboard(List.of(List.of(backButton)));
        return markup;
    }
}
