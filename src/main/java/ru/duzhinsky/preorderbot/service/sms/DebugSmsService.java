package ru.duzhinsky.preorderbot.service.sms;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.logging.Level;

@Log
@Service("DebugSmsSerivce")
public class DebugSmsService implements SmsService {
    @Override
    public void sendSms(String phoneNumber, String text, Consumer<SmsStatus> callback) {
        log.log(Level.WARNING, String.format("Sms for number %s: %s", phoneNumber, text));
        callback.accept(SmsStatus.DELIVERED);
    }
}
