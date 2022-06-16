package ru.duzhinsky.preorderbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;
import ru.duzhinsky.preorderbot.persistence.entities.validationcode.ValidationCode;
import ru.duzhinsky.preorderbot.persistence.entities.validationcode.ValidationCodeRepository;

import java.util.Date;
import java.util.Random;

@Service
public class ValidationCodeService {
    private final ValidationCodeRepository validationCodeRepository;

    @Autowired
    public ValidationCodeService(ValidationCodeRepository validationCodeRepository) {
        this.validationCodeRepository = validationCodeRepository;
    }

    public Integer generate(TgChat chat, String phone) {
        Integer generated = new Random().nextInt(9000)+1000;
        ValidationCode code = new ValidationCode();
        code.setChat(chat);
        code.setCreated(new Date());
        code.setPhone(phone);
        code.setCode(generated);
        validationCodeRepository.save(code);
        return generated;
    }

    public Integer getValidationCode(TgChat chat) {
        return validationCodeRepository.findTopByChatOrderByCreatedDesc(chat).getCode();
    }

    public String getValidationPhone(TgChat chat) {
        return validationCodeRepository.findTopByChatOrderByCreatedDesc(chat).getPhone();
    }
}
