package ru.duzhinsky.preorderbot.bot;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.handlers.*;
import ru.duzhinsky.preorderbot.persistence.entities.TgChat;
import ru.duzhinsky.preorderbot.persistence.entities.dao.DAOFactory;
import ru.duzhinsky.preorderbot.persistence.entities.dao.TgChatDAO;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

public class TelegramUpdateReceiver implements Runnable {
    private final TelegramBot bot;

    public TelegramUpdateReceiver(TelegramBot bot) {
        this.bot = bot;
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
        TgChatDAO chatDAO = DAOFactory.getTgChatDAO();
        TgChat chat = chatDAO.findById(chatId);
        if(chat == null) {
            TgChat newChat = new TgChat();
            newChat.setId(chatId);
            chatDAO.save(newChat);
            chat = newChat;
        }
        if(update.hasMessage() && update.getMessage().getText().equals("/start")) {
            update.getMessage().setText("/SSSTTT");
            chatDAO.update(
                    chat.getId(),
                    c -> c.setChatState(null)
            );
        }
        chatDAO.close();

        System.out.println("MMM UPDATE: " + update);
        System.out.println(chat);
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
