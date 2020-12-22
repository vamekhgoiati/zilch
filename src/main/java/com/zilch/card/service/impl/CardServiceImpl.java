package com.zilch.card.service.impl;

import com.zilch.account.entity.Account;
import com.zilch.account.repository.AccountRepository;
import com.zilch.card.action.CardAction;
import com.zilch.card.dao.CardRepository;
import com.zilch.card.dao.TransactionDao;
import com.zilch.card.dto.CardDTO;
import com.zilch.card.entity.Card;
import com.zilch.card.entity.Transaction;
import com.zilch.card.service.CardService;
import com.zilch.common.exception.InsufficientFundsException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final TransactionDao transactionDao;
    private final Clock clock;

    public CardServiceImpl(CardRepository cardRepository, AccountRepository accountRepository, TransactionDao transactionDao, Clock clock) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.transactionDao = transactionDao;
        this.clock = clock;
    }

    @Override
    public CardDTO findById(Long id) {
        return cardRepository.findById(id).map(CardDTO::toDto).orElseThrow();
    }

    @Override
    public CardDTO saveCard(CardDTO cardDTO) {
        Account account = accountRepository.findById(cardDTO.getAccountId()).orElseThrow();
        Card card = new Card();
        card.setId(cardDTO.getId());
        card.setCardNumber(cardDTO.getCardNumber());
        card.setAccount(account);
        card.setCreationDate(clock.instant());
        card.setBalance(cardDTO.getBalance());
        return CardDTO.toDto(cardRepository.save(card));
    }

    @Override
    public List<CardDTO> getAll() {
        return StreamSupport.stream(cardRepository.findAll().spliterator(), false)
                .map(CardDTO::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CardDTO deleteCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow();
        cardRepository.deleteById(id);
        return CardDTO.toDto(card);
    }

    @Override
    public CardDTO credit(Long id, BigDecimal amount) {
        Card card = cardRepository.findById(id).orElseThrow();
        if (card.getBalance().compareTo(amount) > -1) {
            card.setBalance(card.getBalance().subtract(amount));
            card = cardRepository.save(card);
            saveTransaction(card, amount, CardAction.CREDIT);
            return CardDTO.toDto(card);
        } else {
            throw new InsufficientFundsException("Not enough money");
        }
    }

    @Override
    public CardDTO debit(Long id, BigDecimal amount) {
        Card card = cardRepository.findById(id).orElseThrow();
        card.setBalance(card.getBalance().add(amount));
        card = cardRepository.save(card);
        saveTransaction(card, amount, CardAction.DEBIT);
        return CardDTO.toDto(card);
    }

    private void saveTransaction(Card card, BigDecimal amount, CardAction action) {
        Transaction transaction = new Transaction();
        transaction.setAction(action);
        transaction.setCard(card);
        transaction.setAmount(amount);
        transaction.setTransactionDate(clock.instant());
        transactionDao.save(transaction);
    }
}
