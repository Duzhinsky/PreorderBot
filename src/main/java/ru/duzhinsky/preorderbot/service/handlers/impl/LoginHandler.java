package ru.duzhinsky.preorderbot.service.handlers.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.duzhinsky.preorderbot.bot.PreorderBot;
import ru.duzhinsky.preorderbot.bot.TelegramUtils;
import ru.duzhinsky.preorderbot.service.ValidationCodeGenerator;
import ru.duzhinsky.preorderbot.service.handlers.ChatState;
import ru.duzhinsky.preorderbot.service.handlers.UpdateHandler;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChatRepository;
import ru.duzhinsky.preorderbot.service.sms.SmsService;
import ru.duzhinsky.preorderbot.service.sms.SmsStatus;

import java.util.List;

@Service
public class LoginHandler implements UpdateHandler {
    private final TgChatRepository tgChatRepository;
    private final PreorderBot preorderBot;
    private final SmsService smsService;
    private final ValidationCodeGenerator validationCodeGenerator;

    @Autowired
    public LoginHandler(TgChatRepository tgChatRepository, PreorderBot preorderBot, SmsService smsService, ValidationCodeGenerator validationCodeGenerator) {
        this.tgChatRepository = tgChatRepository;
        this.preorderBot = preorderBot;
        this.smsService = smsService;
        this.validationCodeGenerator = validationCodeGenerator;
    }

    @Override
    public void handle(Update update) {
        TgChat chat = tgChatRepository.findById(TelegramUtils.getChatId(update)).get();
        if(chat.getChatState().equals(ChatState.LOGIN)) {
            sendRequestPhoneMessage(chat, "Для входа введите номер телефона");
        } else if(chat.getChatState().equals(ChatState.LOGIN_WAIT_PHONE)) {
            if(!update.hasMessage()) return;
            String message = update.getMessage().getText();
            if(message.equals("Назад")) {
                chat.setChatState(ChatState.AUTHENTICATION);
                tgChatRepository.save(chat);
                preorderBot.getReceiveQueue().add(update);
            } else {
                StringBuilder phone = new StringBuilder();
                for(char c : message.toCharArray())
                    if(Character.isDigit(c))
                        phone.append(c);
                if (phone.charAt(0) == '8') phone.setCharAt(0,'7');
                sendCode(chat, phone.toString());
            }
        }
    }

    private void sendCode(TgChat chat, String phone) {
        if(!isValidPhone(phone)) {
            SendMessage msg = new SendMessage();
            msg.setChatId(chat.getId().toString());
            msg.setText("Неверный формат номера телефона!");
            msg.setReplyMarkup(getBackKeyboard());
            preorderBot.getSendQueue().add(msg);
        } else {
            Integer code = validationCodeGenerator.generate(chat, phone);
            smsService.sendSms(phone, "Your code is: " + code, status -> smsCallback(chat, status));
        }
    }

    private void smsCallback(TgChat chat, SmsStatus status) {

    }

    private boolean isValidPhone(String phone) {
        return phone.matches("7\\d{10}");
    }

    private void sendRequestPhoneMessage(TgChat chat, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chat.getId().toString());
        msg.setText(text);
        msg.setReplyMarkup(getBackKeyboard());
        preorderBot.getSendQueue().add(msg);
        chat.setChatState(ChatState.LOGIN_WAIT_PHONE);
        tgChatRepository.save(chat);
    }

    private ReplyKeyboardMarkup getBackKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);
        KeyboardRow row = new KeyboardRow();
        KeyboardButton backButton = new KeyboardButton();
        backButton.setText("Назад");
        row.add(backButton);
        markup.setKeyboard(List.of(row));
        return markup;
    }

    @Override
    public ChatState getHandlerScope() {
        return ChatState.LOGIN;
    }
}
