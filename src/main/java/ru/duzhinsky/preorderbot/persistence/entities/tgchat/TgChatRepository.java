package ru.duzhinsky.preorderbot.persistence.entities.tgchat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface TgChatRepository extends JpaRepository<TgChat, Long> {
}
