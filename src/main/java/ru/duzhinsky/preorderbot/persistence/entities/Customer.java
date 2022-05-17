package ru.duzhinsky.preorderbot.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

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

    @ToString.Exclude
    @OneToMany(mappedBy = "customer")
    private Set<TgChat> tgChats;
}