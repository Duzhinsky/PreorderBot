package ru.duzhinsky.preorderbot.bot.handlers;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.TelegramBot;
import ru.duzhinsky.preorderbot.persistence.entities.TgChat;
import ru.duzhinsky.preorderbot.persistence.entities.dao.DAOFactory;
import ru.duzhinsky.preorderbot.persistence.entities.dao.TgChatDAO;

public class DefaultChatHandler implements TelegramChatHandler {
    private final TelegramBot bot;
    private final TgChatDAO chatDAO;

    public DefaultChatHandler(TelegramBot bot) {
        this.bot = bot;
        this.chatDAO = DAOFactory.getTgChatDAO();
    }

    @Override
    public void handle(Update upd) {
        Long chatId = getChatId(upd);
        TgChat chat = chatDAO.findById(chatId);
        if(chat.getCustomer() == null)
            chatDAO.update(
                    chat.getId(),
                    c -> c.setChatState(ChatState.AUTHENTICATION),
                    c -> c.setChatHandlerState((short)0)
            );
        else
            chatDAO.update(
                    chat.getId(),
                    c -> c.setChatState(ChatState.MAIN_MENU),
                    c -> c.setChatHandlerState((short)0)
            );
        chatDAO.close();
        bot.getReceiveQueue().add(upd);
    }
}
