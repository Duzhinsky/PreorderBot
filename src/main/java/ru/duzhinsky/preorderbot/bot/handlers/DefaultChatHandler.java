package ru.duzhinsky.preorderbot.bot.handlers;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.TelegramBot;
import ru.duzhinsky.preorderbot.persistence.entities.TgChat;
import ru.duzhinsky.preorderbot.persistence.dao.EntityDAO;
import ru.duzhinsky.preorderbot.persistence.dao.JPADAOFactory;

public class DefaultChatHandler implements TelegramChatHandler {
    private final TelegramBot bot;
    private final EntityDAO<TgChat, Long> chatDAO;

    public DefaultChatHandler(TelegramBot bot) {
        this.bot = bot;
        this.chatDAO = new JPADAOFactory().getDao(TgChat.class);
    }

    @Override
    public void handle(Update upd) {
        Long chatId = getChatId(upd);
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
        chatDAO.close();
        bot.getReceiveQueue().add(upd);
    }
}
