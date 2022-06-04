package ru.duzhinsky.preorderbot.persistence.entities.tgchat;

import lombok.*;
import ru.duzhinsky.preorderbot.service.handlers.ChatState;
import ru.duzhinsky.preorderbot.persistence.entities.Customer;

import javax.persistence.*;

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
    @Enumerated(EnumType.STRING)
    private ChatState chatState;

    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customer;
}
