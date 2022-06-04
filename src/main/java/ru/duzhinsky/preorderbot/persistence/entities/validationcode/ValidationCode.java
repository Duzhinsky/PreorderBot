package ru.duzhinsky.preorderbot.persistence.entities.validationcode;

import lombok.*;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name = "authenticationcodes")
@NoArgsConstructor
@EqualsAndHashCode(of="id")
@ToString
public class ValidationCode {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date created;

    @Column(name="phone")
    private String phone;

    @ManyToOne
    @JoinColumn(name="chat_id", nullable=false)
    private TgChat chat;
}
