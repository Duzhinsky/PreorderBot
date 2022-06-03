package ru.duzhinsky.preorderbot.bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChatRepository;

@Configuration
public class BotConfig {
    @Bean
    public UpdateReceiver getReceiver(PreorderBot bot, TgChatRepository repository) {
        UpdateReceiver receiver = new UpdateReceiver(bot, repository);
        Thread receiverThread = new Thread(receiver, "Update receiver");
        receiverThread.start();
        return receiver;
    }

    @Bean
    public UpdatesSender getSender(PreorderBot bot) {
        UpdatesSender sender = new UpdatesSender(bot);
        Thread senderThread = new Thread(sender, "Update sender");
        senderThread.start();
        return sender;
    }
}
