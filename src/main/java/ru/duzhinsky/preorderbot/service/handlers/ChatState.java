package ru.duzhinsky.preorderbot.service.handlers;

public enum ChatState {
    DEFAULT,

    AUTHENTICATION,
    AUTHENTICATION_WAIT_REPLY,

    LOGIN,
    LOGIN_WAIT_PHONE,
    LOGIN_WAIT_CODE,

    REGISTRATION,
    REGISTRATION_MENU,
    REGISTRATION_WAIT_PHONE,
    REGISTRATION_WAIT_CODE,
    REGISTRATION_WAIT_NAME,
    REGISTRATION_WAIT_BIRTHDAY,

    MAIN_MENU
}
