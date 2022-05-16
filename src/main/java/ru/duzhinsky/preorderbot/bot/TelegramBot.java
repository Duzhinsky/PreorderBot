package ru.duzhinsky.preorderbot.bot;

import jakarta.persistence.EntityManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.config.Config;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TelegramBot extends TelegramLongPollingBot {
    private static final String BOT_TOKEN;
    private static final String BOT_USERNAME;

    private final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Object> receiveQueue = new ConcurrentLinkedQueue<>();

    static {
        BOT_TOKEN = Config.getProperty("token","");
        BOT_USERNAME = Config.getProperty("username","");
    }

    public TelegramBot(EntityManager entityManager) {
        TelegramUpdateReceiver receiver = new TelegramUpdateReceiver(this, entityManager);
        Thread receiverThread = new Thread(receiver);
        receiverThread.setDaemon(true);
        receiverThread.setName("MsgReceiver");
        receiverThread.setPriority(3);
        receiverThread.start();

        TelegramUpdatesSender sender = new TelegramUpdatesSender(this);
        Thread senderThread = new Thread(sender);
        senderThread.setDaemon(true);
        senderThread.setName("MsgSender");
        senderThread.setPriority(1);
        senderThread.start();
    }

    public Queue<Object> getSendQueue() {
        return sendQueue;
    }

    public Queue<Object> getReceiveQueue() {
        return receiveQueue;
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        receiveQueue.add(update);
    }
}
