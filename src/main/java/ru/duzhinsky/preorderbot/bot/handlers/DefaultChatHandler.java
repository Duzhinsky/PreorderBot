package ru.duzhinsky.preorderbot.bot.handlers;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.TelegramBot;
import ru.duzhinsky.preorderbot.entities.TgChat;
import ru.duzhinsky.preorderbot.entities.repositories.TgChatRepository;

public class DefaultChatHandler implements TelegramChatHandler {
    private final TelegramBot bot;
    private final TgChatRepository chatRepository;

    Weld weld = new Weld();
    WeldContainer container = weld.initialize();

    public DefaultChatHandler(TelegramBot bot) {
        this.bot = bot;
        this.chatRepository = container.select(TgChatRepository.class).get();
    }

    @Override
    public void handle(Update upd) {
        Long chatId = getChatId(upd);
        TgChat chat = chatRepository.findById(chatId);
        if(chat.getCustomer() == null)
            chat.setChatState(ChatState.AUTHENTICATION);
        else
            chat.setChatState(ChatState.MAIN_MENU);
        chat.setChatHandlerState((short)0);
        chatRepository.persist(chat);
        bot.getReceiveQueue().add(upd);

        weld.shutdown();
    }
}
