package ru.duzhinsky.preorderbot.persistence.entities.tgregistration;

import lombok.*;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="tgregistration")
@Getter @Setter
@ToString(exclude = "person")
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class TgRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "chat_id")
    @MapsId
    private TgChat person;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "birthday")
    private Date birthday;
}
