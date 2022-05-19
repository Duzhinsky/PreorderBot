package ru.duzhinsky.preorderbot.sms;

public interface SMSService {
    void sendSMS(String phoneNumber, String text);
}
