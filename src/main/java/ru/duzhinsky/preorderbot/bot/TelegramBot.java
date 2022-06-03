package ru.duzhinsky.preorderbot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Scope("singleton")
public class TelegramBot extends TelegramLongPollingBot {
    private final String username;
    private final String token;

    private final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Update> receiveQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    public TelegramBot(BotConfig config) {
        this.username = config.getUsername();
        this.token = config.getToken();
    }

    public Queue<Object> getSendQueue() {
        return sendQueue;
    }

    public Queue<Update> getReceiveQueue() {
        return receiveQueue;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        receiveQueue.add(update);
    }
}
