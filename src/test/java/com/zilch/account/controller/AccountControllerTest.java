package com.zilch.account.controller;

import com.zilch.account.dto.AccountDTO;
import com.zilch.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(AccountController.class)
public class AccountControllerTest {

    private static final Long ACCOUNT_ID = 1L;
    private static final Long ACCOUNT_ID2 = 2L;
    private static final String FIRST_NAME = "name";
    private static final String LAST_NAME = "lastname";

    @MockBean
    private AccountService accountService;

    @Autowired
    private WebTestClient webTestClient;

    @Captor
    private ArgumentCaptor<AccountDTO> accountArgumentCaptor;

    @Test
    void testCreateAccount() {
        AccountDTO accountDTO = createAccountDto(null);

        when(accountService.save(any())).thenReturn(accountDTO);

        webTestClient.post()
                .uri("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(accountDTO))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        verify(accountService).save(accountArgumentCaptor.capture());

        AccountDTO accountParam = accountArgumentCaptor.getValue();
        assertEquals(FIRST_NAME, accountParam.getFirstName());
        assertEquals(LAST_NAME, accountParam.getLastName());
    }

    @Test
    void testGetAccount() {
        AccountDTO accountDTO = createAccountDto(ACCOUNT_ID);

        when(accountService.findById(ACCOUNT_ID)).thenReturn(accountDTO);

        AccountDTO responseBody = webTestClient.get()
                .uri("/accounts/{id}", ACCOUNT_ID)
                .exchange()
                .expectBody(AccountDTO.class)
                .returnResult()
                .getResponseBody();

        verify(accountService).findById(ACCOUNT_ID);

        assertNotNull(responseBody);
        assertEquals(ACCOUNT_ID, responseBody.getId());
        assertEquals(FIRST_NAME, responseBody.getFirstName());
        assertEquals(LAST_NAME, responseBody.getLastName());
    }

    @Test
    void testGetAccountReturnsNotFoundForNoSuchElementException() {
        when(accountService.findById(ACCOUNT_ID)).thenThrow(new NoSuchElementException());
        webTestClient.get()
                .uri("/accounts/{id}", ACCOUNT_ID)
                .exchange()
                .expectStatus()
                .isNotFound();
        verify(accountService).findById(ACCOUNT_ID);
    }

    @Test
    void testGetAccounts() {
        AccountDTO accountDTO = createAccountDto(ACCOUNT_ID);
        AccountDTO accountDTO2 = createAccountDto(ACCOUNT_ID2);

        when(accountService.getAll()).thenReturn(Arrays.asList(accountDTO, accountDTO2));

        List<AccountDTO> responseBody = webTestClient.get()
                .uri("/accounts")
                .exchange()
                .expectBodyList(AccountDTO.class)
                .returnResult()
                .getResponseBody();

        verify(accountService).getAll();

        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void testDeleteAccount() {
        AccountDTO accountDTO = createAccountDto(ACCOUNT_ID);

        when(accountService.deleteAccount(ACCOUNT_ID)).thenReturn(accountDTO);

        AccountDTO responseBody = webTestClient.delete()
                .uri("/accounts/{id}", ACCOUNT_ID)
                .exchange()
                .expectBody(AccountDTO.class)
                .returnResult()
                .getResponseBody();

        verify(accountService).deleteAccount(ACCOUNT_ID);

        assertNotNull(responseBody);
        assertEquals(ACCOUNT_ID, responseBody.getId());
        assertEquals(FIRST_NAME, responseBody.getFirstName());
        assertEquals(LAST_NAME, responseBody.getLastName());
    }

    @Test
    void testDeleteAccountReturnsNotFoundForNoSuchElementException() {
        when(accountService.deleteAccount(ACCOUNT_ID)).thenThrow(new NoSuchElementException());
        webTestClient.delete()
                .uri("/accounts/{id}", ACCOUNT_ID)
                .exchange()
                .expectStatus()
                .isNotFound();
        verify(accountService).deleteAccount(ACCOUNT_ID);
    }

    private AccountDTO createAccountDto(Long accountId) {
        return AccountDTO.builder()
                .id(accountId)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }
}
