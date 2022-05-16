package ru.duzhinsky.preorderbot.entities;

import jakarta.persistence.*;
import lombok.*;
import ru.duzhinsky.preorderbot.bot.handlers.ChatState;

@Entity
@Table(name = "tgchat_info")
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of={"id"})
@ToString
public class TgChat {
    @Id
    @Column(name="chat_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "handler")
    @Enumerated(EnumType.ORDINAL)
    private ChatState chatState;

    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customer;
}
