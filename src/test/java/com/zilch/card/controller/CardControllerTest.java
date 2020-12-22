package com.zilch.card.controller;

import com.zilch.card.dto.CardDTO;
import com.zilch.card.dto.MoneyRequest;
import com.zilch.card.service.CardService;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(CardController.class)
public class CardControllerTest {

    private static final Long CARD_ID = 1L;
    private static final Long CARD_ID2 = 3L;
    private static final Long ACCOUNT_ID = 2L;
    private static final String CARD_NUMBER = "1234-4321";

    @MockBean
    private CardService cardService;

    @Captor
    private ArgumentCaptor<CardDTO> cardArgumentCaptor;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCreateCard() {
        CardDTO cardDTO = createCardDto(null);
        when(cardService.saveCard(any())).thenReturn(cardDTO);

        webTestClient.post()
                .uri("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(cardDTO))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        verify(cardService).saveCard(cardArgumentCaptor.capture());

        CardDTO cardParam = cardArgumentCaptor.getValue();
        assertEquals(CARD_NUMBER, cardParam.getCardNumber());
        assertEquals(ACCOUNT_ID, cardParam.getAccountId());
        assertEquals(BigDecimal.TEN, cardParam.getBalance());
    }

    @Test
    void testGetCard() {
        CardDTO cardDTO = createCardDto(CARD_ID);
        when(cardService.findById(CARD_ID)).thenReturn(cardDTO);

        CardDTO responseBody = webTestClient.get()
                .uri("/cards/{id}", CARD_ID)
                .exchange()
                .expectBody(CardDTO.class)
                .returnResult()
                .getResponseBody();

        verify(cardService).findById(CARD_ID);

        assertNotNull(responseBody);
        assertEquals(CARD_ID, responseBody.getId());
        assertEquals(CARD_NUMBER, responseBody.getCardNumber());
        assertEquals(BigDecimal.TEN, responseBody.getBalance());
        assertEquals(ACCOUNT_ID, responseBody.getAccountId());
    }

    @Test
    void testGetCardReturnsNotFoundForNoSuchElementException() {
        when(cardService.findById(CARD_ID)).thenThrow(new NoSuchElementException());

        webTestClient.get()
                .uri("/cards/{id}", CARD_ID)
                .exchange()
                .expectStatus()
                .isNotFound();

        verify(cardService).findById(CARD_ID);
    }

    @Test
    void testGetCards() {
        CardDTO cardDTO = createCardDto(CARD_ID);
        CardDTO cardDTO2 = createCardDto(CARD_ID2);
        when(cardService.getAll()).thenReturn(Arrays.asList(cardDTO, cardDTO2));

        List<CardDTO> responseBody = webTestClient.get()
                .uri("/cards")
                .exchange()
                .expectBodyList(CardDTO.class)
                .returnResult()
                .getResponseBody();

        verify(cardService).getAll();

        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void testDeleteCard() {
        CardDTO cardDTO = createCardDto(CARD_ID);
        when(cardService.deleteCard(CARD_ID)).thenReturn(cardDTO);

        CardDTO responseBody = webTestClient.delete()
                .uri("/cards/{id}", CARD_ID)
                .exchange()
                .expectBody(CardDTO.class)
                .returnResult()
                .getResponseBody();

        verify(cardService).deleteCard(CARD_ID);

        assertNotNull(responseBody);
        assertEquals(CARD_ID, responseBody.getId());
        assertEquals(CARD_NUMBER, responseBody.getCardNumber());
        assertEquals(BigDecimal.TEN, responseBody.getBalance());
        assertEquals(ACCOUNT_ID, responseBody.getAccountId());
    }

    @Test
    void testDeleteCardReturnsNotFoundForNoSuchElementException() {
        when(cardService.deleteCard(CARD_ID)).thenThrow(new NoSuchElementException());

        webTestClient.delete()
                .uri("/cards/{id}", CARD_ID)
                .exchange()
                .expectStatus()
                .isNotFound();

        verify(cardService).deleteCard(CARD_ID);
    }

    @Test
    void testCardDebit() {
        CardDTO cardDTO = createCardDto(CARD_ID);
        MoneyRequest moneyRequest = new MoneyRequest(BigDecimal.ONE);
        when(cardService.debit(CARD_ID, BigDecimal.ONE)).thenReturn(cardDTO);

        webTestClient.post()
                .uri("/cards/{id}/debit", CARD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(moneyRequest))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        verify(cardService).debit(CARD_ID, BigDecimal.ONE);
    }

    @Test
    void testCardCredit() {
        CardDTO cardDTO = createCardDto(CARD_ID);
        MoneyRequest moneyRequest = new MoneyRequest(BigDecimal.ONE);
        when(cardService.credit(CARD_ID, BigDecimal.ONE)).thenReturn(cardDTO);

        webTestClient.post()
                .uri("/cards/{id}/credit", CARD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(moneyRequest))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        verify(cardService).credit(CARD_ID, BigDecimal.ONE);
    }

    private CardDTO createCardDto(Long cardId) {
        return CardDTO.builder()
                .id(cardId)
                .accountId(ACCOUNT_ID)
                .cardNumber(CARD_NUMBER)
                .balance(BigDecimal.TEN)
                .build();
    }
}
