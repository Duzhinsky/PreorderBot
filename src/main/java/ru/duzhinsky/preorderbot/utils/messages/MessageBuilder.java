package ru.duzhinsky.preorderbot.utils.messages;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class MessageBuilder {
    private Long chatId;
    private String text;
    private ReplyKeyboard markup;

    public MessageBuilder() {
    }

    public MessageBuilder setChatId(Long chatId) {
        this.chatId = chatId;
        return this;
    }

    public MessageBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public MessageBuilder setReplyMarkup(ReplyKeyboard markup) {
        this.markup = markup;
        return this;
    }

    public SendMessage build() {
        if(chatId == null || text == null) throw new MessageBuildingException();
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        if(markup != null) message.setReplyMarkup(markup);
        return message;
    }
}
