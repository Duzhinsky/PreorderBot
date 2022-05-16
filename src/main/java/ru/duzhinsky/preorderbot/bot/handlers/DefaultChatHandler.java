package ru.duzhinsky.preorderbot.bot.handlers;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.bot.TelegramBot;

import java.util.Queue;

public class DefaultChatHandler implements TelegramChatHandler {
    private final Queue<Object> sendQueue;

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
