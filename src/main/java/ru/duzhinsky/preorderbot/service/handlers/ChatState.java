package ru.duzhinsky.preorderbot.service.handlers;

public enum ChatState {
    DEFAULT,
    AUTHENTICATION,
    AUTHENTICATION_WAIT_REPLY,
    LOGIN,
    LOGIN_WAIT_PHONE,
    LOGIN_WAIT_CODE,
    REGISTRATION,
    MAIN_MENU
}
