package ru.duzhinsky.preorderbot.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class TelegramKeyboards {

    public static InlineKeyboardMarkup loginOrRegisterKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton loginButton = new InlineKeyboardButton();
        loginButton.setText("Войти");
        loginButton.setCallbackData("LOGIN_BUTTON");
        InlineKeyboardButton registerButton = new InlineKeyboardButton();
        registerButton.setText("Регистрация");
        registerButton.setCallbackData("REGISTER_BUTTON");
        markup.setKeyboard(List.of(List.of(loginButton), List.of(registerButton)));
        return markup;
    }
}
