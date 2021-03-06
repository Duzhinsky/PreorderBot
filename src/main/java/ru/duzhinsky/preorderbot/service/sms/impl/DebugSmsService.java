package ru.duzhinsky.preorderbot.service.sms.impl;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import ru.duzhinsky.preorderbot.service.sms.SmsService;
import ru.duzhinsky.preorderbot.service.sms.SmsStatus;

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
