package ru.duzhinsky.preorderbot.service.handlers.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.duzhinsky.preorderbot.bot.PreorderBot;
import ru.duzhinsky.preorderbot.service.handlers.ChatState;
import ru.duzhinsky.preorderbot.service.handlers.UpdateHandler;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChatRepository;

import java.util.List;

@Service
public class AuthenticationHandler implements UpdateHandler {
    private final TgChatRepository tgChatRepository;
    private final PreorderBot bot;

    @Autowired
    public AuthenticationHandler(TgChatRepository tgChatRepository, PreorderBot bot) {
        this.tgChatRepository = tgChatRepository;
        this.bot = bot;
    }

    @Override
    public void handle(TgChat chat, Update update) {
        ChatState state = chat.getChatState();
        if(state == ChatState.AUTHENTICATION) {
            sendAuthMessage(chat);
            chat.setChatState(ChatState.AUTHENTICATION_WAIT_REPLY);
            tgChatRepository.save(chat);
        } else if(state == ChatState.AUTHENTICATION_WAIT_REPLY) {
            if(update == null) return;
            if(!update.hasMessage()) return;
            String message = update.getMessage().getText();
            if(message.equals("Войти")) {
                chat.setChatState(ChatState.LOGIN);
                tgChatRepository.save(chat);
                bot.getRedirectionQueue().add(chat);
            } else if(message.equals("Регистрация")) {
                chat.setChatState(ChatState.REGISTRATION);
                tgChatRepository.save(chat);
                bot.getRedirectionQueue().add(chat);
            }
        }
    }

    private void sendAuthMessage(TgChat chat) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chat.getId().toString());
        msg.setText("Похоже, вы используете телеграм бот для заказа впервые\n" +
                "Если вы уже пользовались нашими сервисами, войдите по номеру телефона");
        msg.setReplyMarkup(loginOrRegisterKeyboard());
        bot.getSendQueue().add(msg);
    }

    private static ReplyKeyboardMarkup loginOrRegisterKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        KeyboardRow row = new KeyboardRow();
        KeyboardButton loginButton = new KeyboardButton();
        loginButton.setText("Войти");
        row.add(loginButton);
        KeyboardButton registerButton = new KeyboardButton();
        registerButton.setText("Регистрация");
        row.add(registerButton);
        markup.setKeyboard(List.of(row));
        return markup;
    }

    @Override
    public List<ChatState> getHandlerScope() {
        return List.of(
                ChatState.AUTHENTICATION,
                ChatState.AUTHENTICATION_WAIT_REPLY
        );
    }
}
