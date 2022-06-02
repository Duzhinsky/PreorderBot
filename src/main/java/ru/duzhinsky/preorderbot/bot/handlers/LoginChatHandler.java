package ru.duzhinsky.preorderbot.bot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.duzhinsky.preorderbot.bot.TelegramBot;
import ru.duzhinsky.preorderbot.bot.updates.ChatUpdate;
import ru.duzhinsky.preorderbot.persistence.entities.TgChat;
import ru.duzhinsky.preorderbot.persistence.dao.EntityDao;
import ru.duzhinsky.preorderbot.persistence.dao.JpaDaoFactory;

import java.util.List;

public class LoginChatHandler extends TelegramChatHandler {
    private static final String requestPhoneText =
            "Для входа введите номер телефона";

    private enum State {
        REQUEST_PHONE,
        INPUT_PHONE,
        VERIFY_PHONE
    }
    private State state;
    private TgChat chat;
    private EntityDao<TgChat, Long> chatDAO;

    public LoginChatHandler(TelegramBot bot, Short stateOrdinal) {
        super(bot, stateOrdinal);
    }

    @Override
    public void init() {
        chatDAO = new JpaDaoFactory<TgChat, Long>().getDao(TgChat.class);
        if(stateOrdinal == null) stateOrdinal = 0;
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
        if(state == State.REQUEST_PHONE) {
            requestPhone(upd);
            chatDAO.update(
                    chat,
                    c -> c.setChatHandlerState((short)State.INPUT_PHONE.ordinal())
            );
            bot.getReceiveQueue().add(upd);
        } else if(state == State.INPUT_PHONE && upd.getContent() instanceof Update) {
            Update update = (Update)upd.getContent();
            if(update.hasCallbackQuery()) {
                String option = update.getCallbackQuery().getData();
                if("BACK_TO_AUTH".equals(option)) {
                    chatDAO.update(
                            chat,
                            c -> c.setChatState(ChatState.AUTHENTICATION),
                            c -> c.setChatHandlerState((short)0)
                    );
                    bot.getReceiveQueue().add(upd);
                }
            }
            if(update.hasMessage())
                checkPhone(upd);
        }
        chatDAO.close();
    }

    private void checkPhone(ChatUpdate<?> upd) {
    }

    private void requestPhone(ChatUpdate<?> upd) {
        SendMessage msg = new SendMessage();
        msg.setChatId(upd.getChatId().toString());
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
