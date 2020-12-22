package com.zilch.account.service;

import com.zilch.account.dto.AccountDTO;
import com.zilch.account.entity.Account;
import com.zilch.account.repository.AccountRepository;
import com.zilch.account.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
public class AccountServiceTest {

    private static final Long ACCOUNT_ID = 1L;
    private static final String FIRST_NAME = "name";
    private static final String LAST_NAME = "lastname";

    @Mock
    private AccountRepository accountRepository;

    @Captor
    private ArgumentCaptor<Account> accountArgumentCaptor;

    private AccountService accountService;

    @BeforeEach
    void init() {
        accountService = new AccountServiceImpl(accountRepository);
    }

    @Test
    void testSaveAccount() {
        when(accountRepository.save(any())).thenReturn(createAccount());
        accountService.save(createAccountDTO());
        verify(accountRepository).save(accountArgumentCaptor.capture());

        Account account = accountArgumentCaptor.getValue();
        assertEquals(FIRST_NAME, account.getFirstName());
        assertEquals(LAST_NAME, account.getLastName());
    }

    @Test
    void testFindById() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(createAccount()));
        accountService.findById(ACCOUNT_ID);
        verify(accountRepository).findById(ACCOUNT_ID);
    }

    @Test
    void testFindByIdThrowsExceptionWhenAccountNotFound() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> accountService.findById(ACCOUNT_ID));
        verify(accountRepository).findById(ACCOUNT_ID);
    }

    @Test
    void testFindAll() {
        when(accountRepository.findAll()).thenReturn(Collections.singletonList(createAccount()));
        List<AccountDTO> accounts = accountService.getAll();
        assertEquals(1, accounts.size());
    }

    @Test
    void testDelete() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(createAccount()));
        accountService.deleteAccount(ACCOUNT_ID);
        verify(accountRepository).deleteById(ACCOUNT_ID);
    }

    @Test
    void testDeleteThrowsExceptionWhenAccountNotFound() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> accountService.deleteAccount(ACCOUNT_ID));
        verify(accountRepository).findById(ACCOUNT_ID);
        verify(accountRepository, never()).deleteById(ACCOUNT_ID);
    }

    private AccountDTO createAccountDTO() {
        return AccountDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    private Account createAccount() {
        Account account = new Account();
        account.setId(ACCOUNT_ID);
        account.setFirstName(FIRST_NAME);
        account.setLastName(LAST_NAME);

        return account;
    }
}
