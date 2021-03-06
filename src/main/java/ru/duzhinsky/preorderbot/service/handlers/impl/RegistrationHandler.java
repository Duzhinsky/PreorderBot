package ru.duzhinsky.preorderbot.service.handlers.impl;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
                checkPhone(chat);
                break;
            case REGISTRATION_WAIT_CODE:
                checkPhoneCode(chat);
                break;
            case REGISTRATION_WAIT_BIRTHDAY:
                checkBirthday(chat);
                break;
            default:
                // todo log error
        }
    }

    private void checkName(TgChat chat) {
    }

    private void checkBirthday(TgChat chat) {
    }

    private void checkPhoneCode(TgChat chat) {
    }

    private void checkPhone(TgChat chat) {
    }

    private void handleMenuOption(TgChat chat, Update update) {
        if(!update.hasMessage()) return;
        var message = update.getMessage().getText();
        var messageLower = message.toLowerCase();

        if(messageLower.contains("??????")) {
            chat.setChatState(ChatState.REGISTRATION_WAIT_NAME);

        } else if(messageLower.contains("??????????????") || messageLower.contains("??????????")) {

        } else if(messageLower.contains("???????? ????????????????") || messageLower.contains("????")) {

        }
    }

    private void sendMenu(TgChat chat) {
        chat.setChatState(ChatState.REGISTRATION_MENU);
        chatRepository.save(chat);
        bot.getSendQueue().add(getMenuMessage(chat));
    }

    private SendMessage getMenuMessage(TgChat forChat) {
        SendMessage msg = new SendMessage();
        msg.setChatId(forChat.getId().toString());
        msg.setText("?????? ?????????????????????? ?????????????? ?????????????????? ????????????. ???????????????????????? ???????? ???????????????? *");
        msg.setReplyMarkup(getMenuKeyboard(getRegInfo(forChat)));
        return msg;
    }

    private static ReplyKeyboardMarkup getMenuKeyboard(TgRegistration regInfo) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        String[] titles = {
                regInfo.getPhone()    == null ? "?????????? ????????????????*" : "?????????? ????????????????* | " + regInfo.getPhone(),
                regInfo.getName()     == null ? "??????*"            : "??????* | " + regInfo.getName(),
                regInfo.getBirthday() == null ? "???????? ????????????????"   : "???????? ???????????????? | " + regInfo.getBirthday()
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
