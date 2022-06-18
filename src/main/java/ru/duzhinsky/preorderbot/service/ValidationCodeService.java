package ru.duzhinsky.preorderbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;
import ru.duzhinsky.preorderbot.persistence.entities.validationcode.ValidationCode;
import ru.duzhinsky.preorderbot.persistence.entities.validationcode.ValidationCodeRepository;
import ru.duzhinsky.preorderbot.service.sms.SmsService;
import ru.duzhinsky.preorderbot.service.sms.SmsStatus;

import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;

@Service
public class ValidationCodeService {
    private final ValidationCodeRepository validationCodeRepository;
    private final SmsService smsService;

    @Autowired
    public ValidationCodeService(ValidationCodeRepository validationCodeRepository, SmsService smsService) {
        this.validationCodeRepository = validationCodeRepository;
        this.smsService = smsService;
    }

    public void generate(TgChat chat, String phone, Consumer<SmsStatus> callback) {
        Integer generated = new Random().nextInt(9000)+1000;
        ValidationCode code = new ValidationCode();
        code.setChat(chat);
        code.setCreated(new Date());
        code.setPhone(phone);
        code.setCode(generated);
        validationCodeRepository.save(code);
        smsService.sendSms(phone, "Ваш код подтверждения: " + generated, callback);
    }

    public Integer getValidationCode(TgChat chat) {
        return validationCodeRepository.findTopByChatOrderByCreatedDesc(chat).getCode();
    }

    public String getValidationPhone(TgChat chat) {
        return validationCodeRepository.findTopByChatOrderByCreatedDesc(chat).getPhone();
    }
}
