package com.zilch.card;

import com.zilch.account.repository.AccountRepository;
import com.zilch.card.dao.CardRepository;
import com.zilch.card.dao.TransactionDao;
import com.zilch.card.service.CardService;
import com.zilch.card.service.TransactionService;
import com.zilch.card.service.impl.CardServiceImpl;
import com.zilch.card.service.impl.TransactionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import java.time.Clock;

@Configuration
public class CardConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public CardService cardService(CardRepository cardRepository, AccountRepository accountRepository, TransactionDao transactionDao, Clock clock) {
        return new CardServiceImpl(cardRepository, accountRepository, transactionDao, clock);
    }

    @Bean
    public TransactionDao transactionDao(EntityManager em) {
        return new TransactionDao(em);
    }

    @Bean
    public TransactionService transactionService(TransactionDao transactionDao) {
        return new TransactionServiceImpl(transactionDao);
    }
}
