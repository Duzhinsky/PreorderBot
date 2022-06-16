package ru.duzhinsky.preorderbot.persistence.entities.tgregistration;

import org.springframework.data.repository.CrudRepository;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;

public interface TgRegistrationRepository extends CrudRepository<TgRegistration, TgChat> {
}
