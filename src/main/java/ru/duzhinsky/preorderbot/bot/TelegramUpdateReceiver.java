package ru.duzhinsky.preorderbot.bot;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.handlers.DefaultChatHandler;
import ru.duzhinsky.preorderbot.bot.handlers.TelegramChatHandler;
import ru.duzhinsky.preorderbot.bot.handlers.TelegramChatHandlerEnum;
import ru.duzhinsky.preorderbot.db.MySQLDAOFactory;
import ru.duzhinsky.preorderbot.db.TelegramDao;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

import java.sql.SQLException;

public class TelegramUpdateReceiver implements Runnable {
    private final TelegramBot bot;
    private final int WAIT_FOR_NEW_MESSAGE_DELAY = 100;
    private final TelegramDao telegramDao;

    public TelegramUpdateReceiver(TelegramBot bot) throws SQLException {
        this.bot = bot;
        telegramDao = MySQLDAOFactory.getTelegramDao();
    }

    @Override
    public void run() {
        while(true) {
            for (Object object = bot.getReceiveQueue().poll(); object != null; object = bot.getReceiveQueue().poll()) {
                analyze(object);
            }
            try {
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
        TelegramChatHandlerEnum chatHandlerEnum = telegramDao.getChatHandler(chatId);
        TelegramChatHandler chatHandler = getHandler(chatHandlerEnum);
        chatHandler.handle(update);
    }

    private TelegramChatHandler getHandler(TelegramChatHandlerEnum handlerEnum) {
        switch (handlerEnum) {
            case DEFAULT: new DefaultChatHandler(bot);
        }
        return new DefaultChatHandler(bot);
    }
}
