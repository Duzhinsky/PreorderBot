package ru.duzhinsky.preorderbot.bot;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.handlers.*;
import ru.duzhinsky.preorderbot.entities.TgChat;
import ru.duzhinsky.preorderbot.entities.dao.TgChatDAO;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

public class TelegramUpdateReceiver implements Runnable {
    private final TelegramBot bot;
    private final TgChatDAO chatRepository;

    public TelegramUpdateReceiver(TelegramBot bot) {
        this.bot = bot;
        Weld weld = new Weld();
        WeldContainer container = weld.initialize();
        this.chatRepository = container.select(TgChatDAO.class).get();
    }

    @Override
    public void run() {
        while(true) {
            for (Object object = bot.getReceiveQueue().poll(); object != null; object = bot.getReceiveQueue().poll()) {
                analyze(object);
            }
            try {
                int WAIT_FOR_NEW_MESSAGE_DELAY = 100;
                Thread.sleep(WAIT_FOR_NEW_MESSAGE_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void analyze(Object o) {
        if(o instanceof Update) {
            analyzeForUpdate((Update)o);
        }
    }

    private void analyzeForUpdate(Update update) {
        Long chatId = getChatId(update);
        TgChat chat = chatRepository.findById(chatId);
        if(chat == null) {
            TgChat newChat = new TgChat();
            newChat.setId(chatId);
            chatRepository.persist(newChat);
            chat = newChat;
        }
        TelegramChatHandler chatHandler = getHandler(chat.getChatState(), chat.getChatHandlerState());
        new Thread(() -> chatHandler.handle(update)).start();
    }

    private TelegramChatHandler getHandler(ChatState handler, Short handlerState) {
        if(handler == null) return new DefaultChatHandler(bot);
        switch (handler) {
            case AUTHENTICATION:
                return new AuthenticationChatHandler(bot, handlerState);
            case LOGIN:
                return new LoginChatHandler(bot, handlerState);
            case REGISTRATION:
                return new RegistrationChatHandler(bot, handlerState);
            default:
                return new DefaultChatHandler(bot);
        }
    }
}
