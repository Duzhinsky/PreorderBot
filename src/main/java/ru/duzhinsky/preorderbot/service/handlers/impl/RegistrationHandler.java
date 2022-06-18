package ru.duzhinsky.preorderbot.service.handlers.impl;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.duzhinsky.preorderbot.bot.PreorderBot;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChatRepository;
import ru.duzhinsky.preorderbot.persistence.entities.tgregistration.TgRegistration;
import ru.duzhinsky.preorderbot.persistence.entities.tgregistration.TgRegistrationRepository;
import ru.duzhinsky.preorderbot.service.handlers.ChatState;
import ru.duzhinsky.preorderbot.service.handlers.UpdateHandler;
import ru.duzhinsky.preorderbot.utils.Phone;
import ru.duzhinsky.preorderbot.utils.messages.MessageBuilder;

import java.util.ArrayList;
import java.util.List;

@Log
@Service
public class RegistrationHandler implements UpdateHandler {
    private final TgRegistrationRepository registrationRepository;
    private final TgChatRepository chatRepository;
    private final PreorderBot bot;

    @Autowired
    public RegistrationHandler(TgRegistrationRepository registrationRepository, TgChatRepository chatRepository, PreorderBot bot) {
        this.registrationRepository = registrationRepository;
        this.chatRepository = chatRepository;
        this.bot = bot;
    }

    @Override
    public void handle(TgChat chat, Update update) {
        TgRegistration regInfo = getRegInfo(chat);
        var state = chat.getChatState();
        switch (state) {
            case REGISTRATION:
                sendMenu(chat);
                break;
            case REGISTRATION_MENU:
                handleMenuOption(chat, update);
                break;
            case REGISTRATION_WAIT_NAME:
                checkName(chat);
                break;
            case REGISTRATION_WAIT_PHONE:
                checkPhone(chat, update);
                break;
            case REGISTRATION_WAIT_CODE:
                checkPhoneCode(chat);
                break;
            case REGISTRATION_WAIT_BIRTHDAY:
                checkBirthday(chat);
                break;
            default:
                log.warning("Registration handler received update he is unable to handle!");
        }
    }

    private void checkName(TgChat chat) {
    }

    private void checkBirthday(TgChat chat) {
    }

    private void checkPhoneCode(TgChat chat) {
    }

    private void checkPhone(TgChat chat, Update update) {
        if(!update.hasMessage()) {
            log.info("Registration handler received update, but there is no message");
            return;
        }
        var message = update.getMessage().getText();
        var phone = Phone.findPhone(message);
        if(phone.isEmpty()) {
            sendPhoneTip(chat.getId());
        } else {

        }
    }

    private void sendPhoneTip(Long chatId) {
        bot.addMessage(
                new MessageBuilder()
                        .setChatId(chatId)
                        .setText("Не удалось распознать номер\n" +
                                "Подсказка: номер телефона лучше всего вводить в формате +7 ххх ххх хххх")
        );
    }

    private void handleMenuOption(TgChat chat, Update update) {
        if(!update.hasMessage()) {
            log.info("Registration handler received update, but there is no message");
            return;
        }

        var message = update.getMessage().getText();
        var messageLower = message.toLowerCase();

        if(messageLower.contains("имя")) {
            chat.setChatState(ChatState.REGISTRATION_WAIT_NAME);
            chatRepository.save(chat);
            bot.addMessage(
                    new MessageBuilder()
                            .setChatId(chat.getId())
                            .setText("Введите ваше имя, на которое будут оформляться заказы, в ответ на это сообщение")
                            .setReplyMarkup(getBackKeyboard())
            );
        } else if(messageLower.contains("телефон") || messageLower.contains("номер")) {
            chat.setChatState(ChatState.REGISTRATION_WAIT_PHONE);
            chatRepository.save(chat);
            bot.addMessage(
                    new MessageBuilder()
                            .setChatId(chat.getId())
                            .setText("Введите номер телефона в ответ на это сообщение")
                            .setReplyMarkup(getBackKeyboard())
            );
        } else if(messageLower.contains("дата рождения") || messageLower.contains("др")) {
            chat.setChatState(ChatState.REGISTRATION_WAIT_BIRTHDAY);
            chatRepository.save(chat);
            bot.addMessage(
                    new MessageBuilder()
                            .setChatId(chat.getId())
                            .setText("Введите дату рождения в ответ на это сообщение")
                            .setReplyMarkup(getBackKeyboard())
            );
        }
    }

    private static ReplyKeyboardMarkup getBackKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton("Назад");
        row.add(button);

        markup.setKeyboard(List.of(row));
        return markup;
    }

    private void sendMenu(TgChat chat) {
        chat.setChatState(ChatState.REGISTRATION_MENU);
        chatRepository.save(chat);
        bot.addMessage(
                new MessageBuilder()
                        .setChatId(chat.getId())
                        .setText("Для регистрации введите следующие данные. Обязательные поля отмечены *")
                        .setReplyMarkup(getMenuKeyboard(getRegInfo(chat)))
        );
    }

    private static ReplyKeyboardMarkup getMenuKeyboard(TgRegistration regInfo) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        String[] titles = {
                regInfo.getPhone()    == null ? "Номер телефона*" : "Номер телефона* | " + regInfo.getPhone(),
                regInfo.getName()     == null ? "Имя*"            : "Имя* | " + regInfo.getName(),
                regInfo.getBirthday() == null ? "Дата рождения"   : "Дата рождения | " + regInfo.getBirthday()
        };

        List<KeyboardRow> rows = new ArrayList<>();
        for(String title : titles) {
            KeyboardRow row = new KeyboardRow();
            KeyboardButton button = new KeyboardButton();
            button.setText(title);
            row.add(button);
            rows.add(row);
        }

        markup.setKeyboard(rows);
        return markup;
    }

    private TgRegistration getRegInfo(TgChat chat) {
        if(chat.getRegInfo() != null) return chat.getRegInfo();
        TgRegistration info = new TgRegistration();
        info.setId(chat.getId());
        info.setPerson(chat);
        registrationRepository.save(info);
        return info;
    }

    @Override
    public List<ChatState> getHandlerScope() {
        return List.of(
                ChatState.REGISTRATION,
                ChatState.REGISTRATION_MENU,
                ChatState.REGISTRATION_WAIT_CODE,
                ChatState.REGISTRATION_WAIT_NAME,
                ChatState.REGISTRATION_WAIT_PHONE,
                ChatState.REGISTRATION_WAIT_BIRTHDAY
        );
    }
}
