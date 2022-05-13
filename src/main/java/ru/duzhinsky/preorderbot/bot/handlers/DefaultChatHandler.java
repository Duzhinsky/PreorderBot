package ru.duzhinsky.preorderbot.bot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.TelegramBot;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

import java.util.Queue;

public class DefaultChatHandler implements TelegramChatHandler {
    private Queue<Object> sendQueue;

    public DefaultChatHandler(TelegramBot bot) {
        this.sendQueue = bot.getSendQueue();
    }

    @Override
    public void handle(Update upd) {
        SendMessage msg = new SendMessage();
        msg.setChatId(getChatId(upd).toString());
        msg.setText("Echo: " + upd.getMessage().getText());
        sendQueue.add(msg);
    }
}
