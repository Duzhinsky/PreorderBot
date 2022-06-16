package ru.duzhinsky.preorderbot.persistence.entities.customer;

import lombok.*;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "customer_info")
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of={"id"})
@ToString
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "phone", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "birthday")
    @Temporal(TemporalType.DATE)
    private Date birthday;

    @ToString.Exclude
    @OneToMany(mappedBy = "customer")
    private Set<TgChat> tgChats;
}
