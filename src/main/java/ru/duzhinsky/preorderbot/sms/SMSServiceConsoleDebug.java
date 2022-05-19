package ru.duzhinsky.preorderbot.sms;

public class SMSServiceConsoleDebug implements SMSService {
    @Override
    public void sendSMS(String phoneNumber, String text) {
        System.out.println("Send SMS to " + phoneNumber + " with text: " + text);
    }
}
