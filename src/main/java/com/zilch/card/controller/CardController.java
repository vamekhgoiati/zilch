package com.zilch.card.controller;

import com.zilch.card.dto.MoneyRequest;
import com.zilch.card.dto.CardDTO;
import com.zilch.card.service.CardService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("{id}")
    public Mono<CardDTO> getCard(@PathVariable Long id) {
        return Mono.just(cardService.findById(id));
    }

    @GetMapping
    public Flux<CardDTO> getCards() {
        return Flux.fromIterable(cardService.getAll());
    }

    @PostMapping
    public Mono<CardDTO> createCard(@Valid @RequestBody CardDTO cardDTO) {
        return Mono.just(cardService.saveCard(cardDTO));
    }

    @DeleteMapping("{id}")
    public Mono<CardDTO> deleteCard(@PathVariable Long id) {
        return Mono.just(cardService.deleteCard(id));
    }

    @PostMapping("{id}/credit")
    public Mono<CardDTO> credit(@PathVariable Long id, @Valid @RequestBody MoneyRequest moneyRequest) {
        return Mono.just(cardService.credit(id, moneyRequest.getAmount()));
    }

    @PostMapping("{id}/debit")
    public Mono<CardDTO> debit(@PathVariable Long id, @Valid @RequestBody MoneyRequest moneyRequest) {
        return Mono.just(cardService.debit(id, moneyRequest.getAmount()));
    }
}
