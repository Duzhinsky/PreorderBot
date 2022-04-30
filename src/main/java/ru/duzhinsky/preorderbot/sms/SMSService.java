package ru.duzhinsky.preorderbot.sms;

import java.net.URISyntaxException;

public interface SMSService {
    void sendSMS(String phoneNumber, String text) throws URISyntaxException;
}
