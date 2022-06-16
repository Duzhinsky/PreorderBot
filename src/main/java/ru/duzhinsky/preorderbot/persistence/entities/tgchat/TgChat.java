package ru.duzhinsky.preorderbot.persistence.entities.tgchat;

import lombok.*;
import ru.duzhinsky.preorderbot.persistence.entities.tgregistration.TgRegistration;
import ru.duzhinsky.preorderbot.service.handlers.ChatState;
import ru.duzhinsky.preorderbot.persistence.entities.customer.Customer;

import javax.persistence.*;

@Entity
@Table(name = "tgchat_info")
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of={"id"})
@ToString(exclude = "regInfo")
public class TgChat {
    @Id
    @Column(name= "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "handler")
    @Enumerated(EnumType.STRING)
    private ChatState chatState;

    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id", referencedColumnName = "chat_id")
    private TgRegistration regInfo;
}
