package com.zilch.card.service;

import com.zilch.account.entity.Account;
import com.zilch.account.repository.AccountRepository;
import com.zilch.card.action.CardAction;
import com.zilch.card.dao.CardRepository;
import com.zilch.card.dao.TransactionDao;
import com.zilch.card.dto.CardDTO;
import com.zilch.card.entity.Card;
import com.zilch.card.entity.Transaction;
import com.zilch.card.service.impl.CardServiceImpl;
import com.zilch.common.exception.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    private static final Long CARD_ID = 2L;
    private static final Long ACCOUNT_ID = 1L;
    private static final Long NON_EXISTENT_ACCOUNT_ID = 3L;
    private static final String ACCOUNT_NAME = "name";
    private static final String ACCOUNT_LASTNANE = "lastname";
    private static final String CARD_NUMBER = "1234-5678";
    private static final String FIXED_DATE = "2020-01-01T09:00:00.00Z";

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionDao transactionDao;

    @Captor
    private ArgumentCaptor<Card> cardArgumentCaptor;

    @Captor
    private ArgumentCaptor<Transaction> transactionArgumentCaptor;

    private CardService cardService;

    @BeforeEach
    void init() {
        Clock clock = Clock.fixed(Instant.parse(FIXED_DATE), ZoneOffset.UTC);
        cardService = new CardServiceImpl(cardRepository, accountRepository, transactionDao, clock);
    }

    @Test
    void testCreateCard() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(createAccount()));
        when(cardRepository.save(any())).thenReturn(createEmptyCard());

        CardDTO cardDTO = createCardDTO(ACCOUNT_ID);
        cardService.saveCard(cardDTO);
        verify(accountRepository).findById(ACCOUNT_ID);
        verify(cardRepository).save(cardArgumentCaptor.capture());

        Card card = cardArgumentCaptor.getValue();
        assertEquals(CARD_NUMBER, card.getCardNumber());
        assertEquals(ACCOUNT_ID, card.getAccount().getId());
        assertEquals(Instant.parse(FIXED_DATE), card.getCreationDate());
        assertEquals(BigDecimal.ONE, card.getBalance());
    }

    @Test
    void testCreateCardThrowsExceptionWhenAccountDoesNotExist() {
        when(accountRepository.findById(NON_EXISTENT_ACCOUNT_ID)).thenReturn(Optional.empty());

        CardDTO cardDTO = createCardDTO(NON_EXISTENT_ACCOUNT_ID);
        assertThrows(NoSuchElementException.class, () -> cardService.saveCard(cardDTO));
        verify(accountRepository).findById(NON_EXISTENT_ACCOUNT_ID);
    }

    @Test
    void testFindById() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(createEmptyCard()));
        cardService.findById(CARD_ID);
        verify(cardRepository).findById(CARD_ID);
    }

    @Test
    void testFindByIdThrowsExceptionWhenCardNotFound() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> cardService.findById(CARD_ID));
        verify(cardRepository).findById(CARD_ID);
    }

    @Test
    void testDelete() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(createEmptyCard()));
        cardService.deleteCard(CARD_ID);
        verify(cardRepository).deleteById(CARD_ID);
    }

    @Test
    void testDeleteThrowsExceptionWhenCardNotFound() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> cardService.deleteCard(CARD_ID));
        verify(cardRepository).findById(CARD_ID);
        verify(cardRepository, never()).deleteById(CARD_ID);
    }

    @Test
    void testCredit() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(createCardWithBalance(BigDecimal.TEN)));
        when(cardRepository.save(any())).thenReturn(createEmptyCard());
        cardService.credit(CARD_ID, BigDecimal.ONE);
        verify(cardRepository).save(cardArgumentCaptor.capture());
        verify(transactionDao).save(transactionArgumentCaptor.capture());

        Card card = cardArgumentCaptor.getValue();
        Transaction transaction = transactionArgumentCaptor.getValue();
        assertEquals(BigDecimal.valueOf(9L), card.getBalance());
        assertEquals(CardAction.CREDIT, transaction.getAction());
        assertEquals(BigDecimal.ONE, transaction.getAmount());
        assertEquals(Instant.parse(FIXED_DATE), transaction.getTransactionDate());
    }

    @Test
    void testCreditThrowsNoSuchElementExceptionForInvalidCardId() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> cardService.credit(CARD_ID, BigDecimal.ONE));
        verify(cardRepository).findById(CARD_ID);
        verify(cardRepository, never()).save(any());
        verify(transactionDao, never()).save(any());

    }

    @Test
    void testCreditThrowsInsufficientFundsException() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(createCardWithBalance(BigDecimal.ONE)));
        assertThrows(InsufficientFundsException.class, () -> cardService.credit(CARD_ID, BigDecimal.TEN));
        verify(cardRepository).findById(CARD_ID);
        verify(cardRepository, never()).save(any());
        verify(transactionDao, never()).save(any());

    }

    @Test
    void testDebit() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.of(createCardWithBalance(BigDecimal.TEN)));
        when(cardRepository.save(any())).thenReturn(createEmptyCard());
        cardService.debit(CARD_ID, BigDecimal.ONE);
        verify(cardRepository).save(cardArgumentCaptor.capture());
        verify(transactionDao).save(transactionArgumentCaptor.capture());

        Card card = cardArgumentCaptor.getValue();
        Transaction transaction = transactionArgumentCaptor.getValue();
        assertEquals(BigDecimal.valueOf(11L), card.getBalance());
        assertEquals(CardAction.DEBIT, transaction.getAction());
        assertEquals(BigDecimal.ONE, transaction.getAmount());
        assertEquals(Instant.parse(FIXED_DATE), transaction.getTransactionDate());
    }

    @Test
    void testDebitThrowsNoSuchElementExceptionForInvalidCardId() {
        when(cardRepository.findById(CARD_ID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> cardService.debit(CARD_ID, BigDecimal.ONE));
        verify(cardRepository).findById(CARD_ID);
        verify(cardRepository, never()).save(any());
        verify(transactionDao, never()).save(any());

    }

    @Test
    void testGetAll() {
        when(cardRepository.findAll()).thenReturn(Collections.singletonList(createEmptyCard()));
        List<CardDTO> result = cardService.getAll();
        assertEquals(1, result.size());
        verify(cardRepository).findAll();
    }

    private CardDTO createCardDTO(Long accountId) {
        return CardDTO.builder()
                .accountId(accountId)
                .cardNumber(CARD_NUMBER)
                .balance(BigDecimal.ONE)
                .build();
    }

    private Account createAccount() {
        Account account = new Account();
        account.setId(ACCOUNT_ID);
        account.setFirstName(ACCOUNT_NAME);
        account.setLastName(ACCOUNT_LASTNANE);

        return account;
    }

    private Card createEmptyCard() {
        Card card = new Card();
        card.setAccount(createAccount());
        return card;
    }

    private Card createCardWithBalance(BigDecimal balance) {
        Card card = new Card();
        card.setCardNumber(CARD_NUMBER);
        card.setAccount(createAccount());
        card.setBalance(balance);
        return card;
    }
}
