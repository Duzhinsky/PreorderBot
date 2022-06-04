package ru.duzhinsky.preorderbot.persistence.entities.validationcode;

import org.springframework.data.repository.CrudRepository;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;

public interface ValidationCodeRepository extends CrudRepository<ValidationCode, Integer> {
    ValidationCode findTopByChatOrderByCreatedDesc(TgChat chat);
}
