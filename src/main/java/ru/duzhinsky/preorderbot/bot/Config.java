package ru.duzhinsky.preorderbot.bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChatRepository;

@Configuration
public class Config {
    @Bean
    public TelegramUpdateReceiver getReceiver(TelegramBot bot, TgChatRepository repository) {
        TelegramUpdateReceiver receiver = new TelegramUpdateReceiver(bot, repository);
        Thread receiverThread = new Thread(receiver, "Update receiver");
        receiverThread.start();
        return receiver;
    }

    @Bean
    public TelegramUpdatesSender getSender(TelegramBot bot) {
        TelegramUpdatesSender sender = new TelegramUpdatesSender(bot);
        Thread senderThread = new Thread(sender, "Update sender");
        senderThread.start();
        return sender;
    }
}
