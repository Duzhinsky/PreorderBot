package ru.duzhinsky.preorderbot.bot;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;
import ru.duzhinsky.preorderbot.utils.messages.MessageBuilder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Scope("singleton")
@Getter
public class PreorderBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;

    private final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Update> receiveQueue = new ConcurrentLinkedQueue<>();
    private final Queue<TgChat> redirectionQueue = new ConcurrentLinkedQueue<>();

    @Autowired
    public PreorderBot(BotProperties properties) {
        this.botUsername = properties.getUsername();
        this.botToken = properties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        receiveQueue.add(update);
    }

    public void addMessage(SendMessage msg) { sendQueue.add(msg); }
    public void addMessage(MessageBuilder builder) { sendQueue.add(builder.build()); }
}
