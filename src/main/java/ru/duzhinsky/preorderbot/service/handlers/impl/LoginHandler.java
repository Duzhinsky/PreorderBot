package ru.duzhinsky.preorderbot.service.handlers.impl;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.duzhinsky.preorderbot.bot.PreorderBot;
import ru.duzhinsky.preorderbot.service.ValidationCodeService;
import ru.duzhinsky.preorderbot.service.handlers.ChatState;
import ru.duzhinsky.preorderbot.service.handlers.UpdateHandler;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChatRepository;
import ru.duzhinsky.preorderbot.service.sms.SmsService;
import ru.duzhinsky.preorderbot.service.sms.SmsStatus;

import java.util.List;
import java.util.logging.Level;

@Log
@Service
public class LoginHandler implements UpdateHandler {
    private final TgChatRepository tgChatRepository;
    private final PreorderBot preorderBot;
    private final SmsService smsService;
    private final ValidationCodeService validationCodeService;

    @Autowired
    public LoginHandler(TgChatRepository tgChatRepository,
                        PreorderBot preorderBot,
                        @Qualifier("DebugSmsSerivce") SmsService smsService,
                        ValidationCodeService validationCodeGenerator) {
        this.tgChatRepository = tgChatRepository;
        this.preorderBot = preorderBot;
        this.smsService = smsService;
        this.validationCodeService = validationCodeGenerator;
    }

    @Override
    public void handle(TgChat chat, Update update) {
        var state = chat.getChatState();
        if(state == ChatState.LOGIN) {
            sendRequestPhoneMessage(chat, "Для входа введите номер телефона");
        } else if(state == ChatState.LOGIN_WAIT_PHONE) {
            if(update == null) return;
            if(!update.hasMessage()) return;
            String message = update.getMessage().getText();
            if(message.equals("Назад")) {
                chat.setChatState(ChatState.AUTHENTICATION);
                tgChatRepository.save(chat);
                preorderBot.getRedirectionQueue().add(chat);
            } else {
                StringBuilder phone = new StringBuilder();
                for(char c : message.toCharArray())
                    if(Character.isDigit(c))
                        phone.append(c);
                if (phone.length() > 0 && phone.charAt(0) == '8') phone.setCharAt(0,'7');
                sendCode(chat, phone.toString());
            }
        } else if(state == ChatState.LOGIN_WAIT_CODE) {
            if(update == null) return;
            if(!update.hasMessage()) return;
            String message = update.getMessage().getText();
            if(message.equals("Назад")) {
                chat.setChatState(ChatState.LOGIN);
                tgChatRepository.save(chat);
                preorderBot.getRedirectionQueue().add(chat);
            } else {
                checkCode(chat, message);
            }
        }
    }

    private void checkCode(TgChat chat, String message) {
        try {
            Integer inputCode = Integer.parseInt(message);
            Integer code = validationCodeService.getValidationCode(chat);
            if(inputCode.equals(code)) {
                SendMessage remover = new SendMessage();
                remover.setText("Вход произошел успешно!");
                remover.setChatId(chat.getId().toString());
                ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
                keyboardRemove.setRemoveKeyboard(true);
                remover.setReplyMarkup(keyboardRemove);
                preorderBot.getSendQueue().add(remover);
                chat.setChatState(ChatState.MAIN_MENU);
                tgChatRepository.save(chat);
            } else {
                SendMessage msg = new SendMessage();
                msg.setChatId(chat.getId().toString());
                msg.setText("Неверный код! Попробуйте еще раз!");
                msg.setReplyMarkup(getBackKeyboard());
                preorderBot.getSendQueue().add(msg);
            }
        } catch(NumberFormatException e) {
            SendMessage msg = new SendMessage();
            msg.setChatId(chat.getId().toString());
            msg.setText("Неверный формат кода! Попробуйте еще раз!");
            msg.setReplyMarkup(getBackKeyboard());
            preorderBot.getSendQueue().add(msg);
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
            Integer code = validationCodeService.generate(chat, phone);
            smsService.sendSms(phone, "Your code is: " + code, status -> smsCallback(chat, status));
        }
    }

    private void smsCallback(TgChat chat, SmsStatus status) {
        if(status == SmsStatus.ERROR) {
            log.log(Level.WARNING, String.format("An error occurred while sending a message to the chat %d", chat.getId()));
            SendMessage msg = new SendMessage();
            msg.setChatId(chat.getId().toString());
            msg.setText("Произошла ошибка при отправке кода подтверждения. Попробуйте позже");
            preorderBot.getSendQueue().add(msg);
            chat.setChatState(ChatState.AUTHENTICATION);
            tgChatRepository.save(chat);
            preorderBot.getRedirectionQueue().add(chat);
        } else if(status == SmsStatus.DELIVERED) {
            log.log(Level.WARNING, String.format("SMS to the chat %d was delivered successful", chat.getId()));
            SendMessage msg = new SendMessage();
            msg.setChatId(chat.getId().toString());
            msg.setText("Код для входа был отправлен по вашему номеру телефона. Введите его в ответ на это сообщение.");
            msg.setReplyMarkup(getBackKeyboard());
            preorderBot.getSendQueue().add(msg);
            chat.setChatState(ChatState.LOGIN_WAIT_CODE);
            tgChatRepository.save(chat);
        }
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
