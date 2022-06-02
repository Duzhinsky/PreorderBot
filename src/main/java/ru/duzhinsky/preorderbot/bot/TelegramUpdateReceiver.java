package ru.duzhinsky.preorderbot.bot;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.handlers.*;
import ru.duzhinsky.preorderbot.bot.updates.ChatUpdate;
import ru.duzhinsky.preorderbot.persistence.entities.TgChat;
import ru.duzhinsky.preorderbot.persistence.dao.EntityDao;
import ru.duzhinsky.preorderbot.persistence.dao.JpaDaoFactory;

public class TelegramUpdateReceiver implements Runnable {
    private final TelegramBot bot;

    public TelegramUpdateReceiver(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        while(true) {
            for (ChatUpdate<?> upd = bot.getReceiveQueue().poll(); upd != null; upd = bot.getReceiveQueue().poll()) {
                analyze(upd);
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

    private void analyze(ChatUpdate<?> update) {
        Long chatId = update.getChatId();
        EntityDao<TgChat, Long> chatDAO = new JpaDaoFactory<TgChat, Long>().getDao(TgChat.class);
        TgChat chat = chatDAO.find(chatId);
        if(chat == null) {
            TgChat newChat = new TgChat();
            newChat.setId(chatId);
            chatDAO.persist(newChat);
            chat = newChat;
        }
        if(update.getContent() instanceof Update) {
            Update upd = (Update)update.getContent();
            if(upd.hasMessage() && "/start".equals(upd.getMessage().getText()))
                chatDAO.update(chat, c -> c.setChatState(null));
        }
        chatDAO.close();
        TelegramChatHandler chatHandler = getHandler(chat.getChatState(), chat.getChatHandlerState());
        new Thread(() -> chatHandler.handle(update)).start();
    }

    private TelegramChatHandler getHandler(ChatState handler, Short handlerState) {
        if(handler == null) return new DefaultChatHandler(bot, (short)0);
        switch (handler) {
            case AUTHENTICATION:
                return new AuthenticationChatHandler(bot, handlerState);
            case LOGIN:
                return new LoginChatHandler(bot, handlerState);
            case REGISTRATION:
                return new RegistrationChatHandler(bot, handlerState);
            default:
                return new DefaultChatHandler(bot, (short)0);
        }
    }

}
