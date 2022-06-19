package ru.duzhinsky.preorderbot.service.handlers.impl;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.duzhinsky.preorderbot.bot.PreorderBot;
import ru.duzhinsky.preorderbot.persistence.entities.customer.CustomerRepository;
import ru.duzhinsky.preorderbot.service.ValidationCodeService;
import ru.duzhinsky.preorderbot.service.handlers.ChatState;
import ru.duzhinsky.preorderbot.service.handlers.UpdateHandler;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChatRepository;
import ru.duzhinsky.preorderbot.service.sms.SmsStatus;
import ru.duzhinsky.preorderbot.utils.Phone;
import ru.duzhinsky.preorderbot.utils.messages.MessageBuilder;

import java.util.List;

@Log
@Service
public class LoginHandler implements UpdateHandler {
    private final TgChatRepository tgChatRepository;
    private final CustomerRepository customerRepository;
    private final PreorderBot preorderBot;
    private final ValidationCodeService validationCodeService;

    @Autowired
    public LoginHandler(TgChatRepository tgChatRepository,
                        CustomerRepository customerRepository,
                        PreorderBot preorderBot,
                        ValidationCodeService validationCodeGenerator) {
        this.tgChatRepository = tgChatRepository;
        this.customerRepository = customerRepository;
        this.preorderBot = preorderBot;
        this.validationCodeService = validationCodeGenerator;
    }

    @Override
    public void handle(TgChat chat, Update update) {
        var state = chat.getChatState();
        if(state == ChatState.LOGIN) {
            sendRequestPhoneMessage(chat);
        } else if(state == ChatState.LOGIN_WAIT_PHONE) {
            checkPhone(chat, update);
        } else if(state == ChatState.LOGIN_WAIT_CODE) {
            checkCode(chat, update);
        }
    }

    private void checkPhone(TgChat chat, Update update) {
        if(!update.hasMessage()) return;
        String message = update.getMessage().getText();
        if(message.equals("Назад")) {
            chat.setChatState(ChatState.AUTHENTICATION);
            tgChatRepository.save(chat);
            preorderBot.getRedirectionQueue().add(chat);
        } else {
            sendCode(chat, message);
        }
    }

    private void checkCode(TgChat chat, Update update) {
        if(!update.hasMessage()) return;
        var message = update.getMessage().getText();
        if(message.equals("Назад")) {
            chat.setChatState(ChatState.LOGIN);
            tgChatRepository.save(chat);
            preorderBot.getRedirectionQueue().add(chat);
            return;
        }
        var codeString = message.replaceAll("[^\\d]", "");
        try {
            Integer inputCode = Integer.parseInt(codeString);
            Integer code = validationCodeService.getValidationCode(chat);
            if(inputCode.equals(code)) {
                authorize(chat);
            } else {
                preorderBot.addMessage(
                        new MessageBuilder()
                                .setChatId(chat.getId())
                                .setText("Неверный код! Попробуйте еще раз!")
                                .setReplyMarkup(getBackKeyboard())
                );
            }
        } catch(NumberFormatException e) {
            preorderBot.addMessage(
                    new MessageBuilder()
                            .setChatId(chat.getId())
                            .setText("Неверный формат кода! Попробуйте еще раз!")
                            .setReplyMarkup(getBackKeyboard())
            );
        }
    }

    private void authorize(TgChat chat) {
        customerRepository.findByPhoneNumber(validationCodeService.getValidationPhone(chat)).ifPresentOrElse(
                customer -> {
                    chat.setCustomer(customer);
                    chat.setChatState(ChatState.MAIN_MENU);
                    tgChatRepository.save(chat);
                    preorderBot.addMessage(
                            new MessageBuilder()
                                    .setChatId(chat.getId())
                                    .setText("Вход произошел успешно")
                                    .removeKeyboard()
                    );
                },
                () -> {
                    chat.setChatState(ChatState.AUTHENTICATION);
                    tgChatRepository.save(chat);
                    preorderBot.addMessage(
                            new MessageBuilder()
                                    .setChatId(chat.getId())
                                    .setText("Произошла ошибка! Пользователь с таким номером не найден.")
                                    .removeKeyboard()
                    );
                    preorderBot.getRedirectionQueue().add(chat);
                }
        );
    }

    private void sendCode(TgChat chat, String message) {
        var phone = Phone.findPhone(message);
        if(phone.isEmpty()) {
            preorderBot.addMessage(
                    new MessageBuilder()
                            .setChatId(chat.getId())
                            .setText("Неверный формат номера телефона!")
                            .setReplyMarkup(getBackKeyboard())
            );
        } else {
            if(customerRepository.findByPhoneNumber(phone.get().getPhone()).isEmpty()) {
                preorderBot.addMessage(
                        new MessageBuilder()
                                .setChatId(chat.getId())
                                .setText("Пользователь с таким номером не найден! Введите другой номер телефона.")
                                .setReplyMarkup(getBackKeyboard())
                );
            } else {
                validationCodeService.generate(chat, phone.get().getPhone(), status -> smsCallback(chat, status));
            }
        }
    }

    private void smsCallback(TgChat chat, SmsStatus status) {
        if(status == SmsStatus.ERROR) {
            log.warning(String.format("An error occurred while sending a message to the chat %d", chat.getId()));
            chat.setChatState(ChatState.AUTHENTICATION);
            tgChatRepository.save(chat);
            preorderBot.addMessage(
                new MessageBuilder()
                        .setChatId(chat.getId())
                        .setText("Произошла ошибка при отправке кода подтверждения. Попробуйте позже")
                        .removeKeyboard()
            );
            preorderBot.getRedirectionQueue().add(chat);
        } else if(status == SmsStatus.DELIVERED) {
            log.warning(String.format("SMS to the chat %d was delivered successful", chat.getId()));
            chat.setChatState(ChatState.LOGIN_WAIT_CODE);
            tgChatRepository.save(chat);
            preorderBot.addMessage(
                    new MessageBuilder()
                            .setChatId(chat.getId())
                            .setText("Код для входа был отправлен по вашему номеру телефона. Введите его в ответ на это сообщение.")
                            .setReplyMarkup(getBackKeyboard())
            );
        }
    }

    private void sendRequestPhoneMessage(TgChat chat) {
        chat.setChatState(ChatState.LOGIN_WAIT_PHONE);
        tgChatRepository.save(chat);
        preorderBot.addMessage(
                new MessageBuilder()
                        .setChatId(chat.getId())
                        .setText("Для входа введите номер телефона")
                        .setReplyMarkup(getBackKeyboard())
        );
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
    public List<ChatState> getHandlerScope() {
        return List.of(
                ChatState.LOGIN,
                ChatState.LOGIN_WAIT_CODE,
                ChatState.LOGIN_WAIT_PHONE
        );
    }
}
