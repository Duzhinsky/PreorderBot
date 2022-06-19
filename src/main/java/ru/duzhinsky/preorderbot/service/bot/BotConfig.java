package ru.duzhinsky.preorderbot.service.bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.duzhinsky.preorderbot.bot.PreorderBot;
import ru.duzhinsky.preorderbot.service.handlers.HandlersContext;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChatRepository;

@Configuration
public class BotConfig {
    @Bean
    public UpdateReceiver getReceiver(PreorderBot bot, TgChatRepository repository, HandlersContext context) {
        UpdateReceiver receiver = new UpdateReceiver(bot, context, repository);
        Thread receiverThread = new Thread(receiver, "Update receiver");
        receiverThread.start();
        return receiver;
    }

    @Bean
    public RedirectionReceiver getRedirectionReceiver(PreorderBot bot, HandlersContext handlersContext) {
        RedirectionReceiver receiver = new RedirectionReceiver(bot, handlersContext);
        Thread receiverThread = new Thread(receiver, "Redirections receiver");
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
