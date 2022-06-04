package ru.duzhinsky.preorderbot.service.sms;

import java.util.function.Consumer;

public interface SmsService {
    void sendSms(String phoneNumber, String text, Consumer<SmsStatus> callback);
}
