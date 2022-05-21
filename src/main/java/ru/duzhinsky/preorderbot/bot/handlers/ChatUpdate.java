package ru.duzhinsky.preorderbot.bot.handlers;

import lombok.Getter;
import lombok.Setter;
import ru.duzhinsky.preorderbot.bot.TelegramBot;

@Getter
@Setter
public class ChatUpdate<T> {
    private TelegramBot bot;
    private Long chatId;
    private T content;

    public ChatUpdate(TelegramBot bot, Long chatId, T content) {
        this.bot = bot;
        this.chatId = chatId;
        this.content = content;
    }
}
