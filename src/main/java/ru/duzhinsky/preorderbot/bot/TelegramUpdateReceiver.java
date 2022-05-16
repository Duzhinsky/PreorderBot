package ru.duzhinsky.preorderbot.bot;

import jakarta.persistence.EntityManager;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.handlers.DefaultChatHandler;
import ru.duzhinsky.preorderbot.bot.handlers.TelegramChatHandler;
import ru.duzhinsky.preorderbot.bot.handlers.ChatState;
import ru.duzhinsky.preorderbot.entities.TgChat;
import ru.duzhinsky.preorderbot.entities.repositories.TgChatRepository;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

import java.sql.SQLException;

public class TelegramUpdateReceiver implements Runnable {
    private final TelegramBot bot;
    private final TgChatRepository chatRepository;

    public TelegramUpdateReceiver(TelegramBot bot, EntityManager entityManager) {
        this.bot = bot;
        chatRepository = new TgChatRepository(entityManager);
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
            chatRepository.create(newChat);
            chat = newChat;
        }
        TelegramChatHandler chatHandler = getHandler(chat.getChatState());
        chatHandler.handle(update);
    }

    private TelegramChatHandler getHandler(ChatState handlerEnum) {
        if(handlerEnum == null) return new DefaultChatHandler(bot);
        switch (handlerEnum) {
            case DEFAULT:
                return new DefaultChatHandler(bot);
            default:
                return new DefaultChatHandler(bot);
        }
    }
}
