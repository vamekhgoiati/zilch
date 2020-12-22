package com.zilch.card.entity;

import com.zilch.account.entity.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String cardNumber;
    private Instant creationDate;
    private BigDecimal balance;
    @OneToOne
    private Account account;
    @Version
    private Long version;
}
