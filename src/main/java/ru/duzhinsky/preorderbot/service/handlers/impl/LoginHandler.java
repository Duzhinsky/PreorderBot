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
import ru.duzhinsky.preorderbot.persistence.entities.customer.CustomerRepository;
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
    private final CustomerRepository customerRepository;
    private final PreorderBot preorderBot;
    private final SmsService smsService;
    private final ValidationCodeService validationCodeService;

    @Autowired
    public LoginHandler(TgChatRepository tgChatRepository,
                        CustomerRepository customerRepository,
                        PreorderBot preorderBot,
                        @Qualifier("DebugSmsSerivce") SmsService smsService,
                        ValidationCodeService validationCodeGenerator) {
        this.tgChatRepository = tgChatRepository;
        this.customerRepository = customerRepository;
        this.preorderBot = preorderBot;
        this.smsService = smsService;
        this.validationCodeService = validationCodeGenerator;
    }

    @Override
    public void handle(TgChat chat, Update update) {
        var state = chat.getChatState();
        if(state == ChatState.LOGIN) {
            sendRequestPhoneMessage(chat, "?????? ?????????? ?????????????? ?????????? ????????????????");
        } else if(state == ChatState.LOGIN_WAIT_PHONE) {
            if(update == null) return;
            if(!update.hasMessage()) return;
            String message = update.getMessage().getText();
            if(message.equals("??????????")) {
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
            if(message.equals("??????????")) {
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

                customerRepository.findByPhoneNumber(validationCodeService.getValidationPhone(chat)).ifPresentOrElse(
                        customer -> {
                            chat.setCustomer(customer);
                            chat.setChatState(ChatState.MAIN_MENU);
                            tgChatRepository.save(chat);

                            SendMessage remover = new SendMessage();
                            remover.setText("???????? ?????????????????? ??????????????!");
                            remover.setChatId(chat.getId().toString());
                            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
                            keyboardRemove.setRemoveKeyboard(true);
                            remover.setReplyMarkup(keyboardRemove);
                            preorderBot.getSendQueue().add(remover);
                        },
                        () -> {
                            chat.setChatState(ChatState.AUTHENTICATION);
                            tgChatRepository.save(chat);

                            SendMessage errMess = new SendMessage();
                            errMess.setText("?????????????????? ????????????! ???????????????????????? ?? ?????????? ?????????????? ???? ????????????.");
                            errMess.setChatId(chat.getId().toString());
                            preorderBot.getSendQueue().add(errMess);
                        }
                );
            } else {
                SendMessage msg = new SendMessage();
                msg.setChatId(chat.getId().toString());
                msg.setText("???????????????? ??????! ???????????????????? ?????? ??????!");
                msg.setReplyMarkup(getBackKeyboard());
                preorderBot.getSendQueue().add(msg);
            }
        } catch(NumberFormatException e) {
            SendMessage msg = new SendMessage();
            msg.setChatId(chat.getId().toString());
            msg.setText("???????????????? ???????????? ????????! ???????????????????? ?????? ??????!");
            msg.setReplyMarkup(getBackKeyboard());
            preorderBot.getSendQueue().add(msg);
        }
    }

    private void sendCode(TgChat chat, String phone) {
        if(!isValidPhone(phone)) {
            SendMessage msg = new SendMessage();
            msg.setChatId(chat.getId().toString());
            msg.setText("???????????????? ???????????? ???????????? ????????????????!");
            msg.setReplyMarkup(getBackKeyboard());
            preorderBot.getSendQueue().add(msg);
        } else {
            if(customerRepository.findByPhoneNumber(phone).isEmpty()) {
                SendMessage msg = new SendMessage();
                msg.setChatId(chat.getId().toString());
                msg.setText("???????????????????????? ?? ?????????? ?????????????? ???? ????????????! ?????????????? ???????????? ?????????? ????????????????.");
                msg.setReplyMarkup(getBackKeyboard());
                preorderBot.getSendQueue().add(msg);
            } else {
                Integer code = validationCodeService.generate(chat, phone);
                smsService.sendSms(phone, "Your code is: " + code, status -> smsCallback(chat, status));
            }
        }
    }

    private void smsCallback(TgChat chat, SmsStatus status) {
        if(status == SmsStatus.ERROR) {
            log.log(Level.WARNING, String.format("An error occurred while sending a message to the chat %d", chat.getId()));
            SendMessage msg = new SendMessage();
            msg.setChatId(chat.getId().toString());
            msg.setText("?????????????????? ???????????? ?????? ???????????????? ???????? ??????????????????????????. ???????????????????? ??????????");
            preorderBot.getSendQueue().add(msg);
            chat.setChatState(ChatState.AUTHENTICATION);
            tgChatRepository.save(chat);
            preorderBot.getRedirectionQueue().add(chat);
        } else if(status == SmsStatus.DELIVERED) {
            log.log(Level.WARNING, String.format("SMS to the chat %d was delivered successful", chat.getId()));
            SendMessage msg = new SendMessage();
            msg.setChatId(chat.getId().toString());
            msg.setText("?????? ?????? ?????????? ?????? ?????????????????? ???? ???????????? ???????????? ????????????????. ?????????????? ?????? ?? ?????????? ???? ?????? ??????????????????.");
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
        backButton.setText("??????????");
        row.add(backButton);
        markup.setKeyboard(List.of(row));
        return markup;
    }

    @Override
    public List<ChatState> getHandlerScope() {
        return List.of(
                ChatState.LOGIN,
                ChatState.LOGIN_WAIT_CODE,
                ChatState.LOGIN_WAIT_PHONE
        );
    }
}
