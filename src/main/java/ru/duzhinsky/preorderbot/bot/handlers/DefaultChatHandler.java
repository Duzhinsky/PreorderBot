package ru.duzhinsky.preorderbot.bot.handlers;

import ru.duzhinsky.preorderbot.bot.TelegramBot;
import ru.duzhinsky.preorderbot.bot.updates.ChatUpdate;
import ru.duzhinsky.preorderbot.bot.updates.EmptyUpdate;
import ru.duzhinsky.preorderbot.persistence.entities.TgChat;
import ru.duzhinsky.preorderbot.persistence.dao.EntityDao;
import ru.duzhinsky.preorderbot.persistence.dao.JpaDaoFactory;

public class DefaultChatHandler extends TelegramChatHandler {
    private EntityDao<TgChat, Long> chatDAO;

    public DefaultChatHandler(TelegramBot bot, Short stateOrdinal) {
        super(bot, stateOrdinal);
    }

    @Override
    public void init() {
        chatDAO = new JpaDaoFactory<TgChat, Long>().getDao(TgChat.class);
    }

    @Override
    public void close() {
        chatDAO.close();
    }

    @Override
    public void handleAction(ChatUpdate<?> upd) {
        Long chatId = upd.getChatId();
        TgChat chat = chatDAO.find(chatId);
        if(chat.getCustomer() == null)
            chatDAO.update(
                    chat,
                    c -> c.setChatState(ChatState.AUTHENTICATION),
                    c -> c.setChatHandlerState((short)0)
            );
        else
            chatDAO.update(
                    chat,
                    c -> c.setChatState(ChatState.MAIN_MENU),
                    c -> c.setChatHandlerState((short)0)
            );
        bot.getReceiveQueue().add(new EmptyUpdate(bot, chatId));
    }
}
