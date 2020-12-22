package com.zilch.card.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zilch.card.action.CardAction;
import com.zilch.card.dto.TransactionDTO;
import com.zilch.card.dto.TransactionFilter;
import com.zilch.card.service.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("{id}")
    public Mono<TransactionDTO> getTransaction(@PathVariable Long id) {
        return Mono.just(transactionService.findById(id));
    }

    @GetMapping("filter")
    public Flux<TransactionDTO> getTransactions(@RequestParam(required = false) CardAction action,
                                                @RequestParam(required = false) String cardNumber,
                                                @RequestParam(required = false) BigDecimal amountFrom,
                                                @RequestParam(required = false) BigDecimal amountTo,
                                                @RequestParam(required = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") Instant dateFrom,
                                                @RequestParam(required = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") Instant dateTo,
                                                @RequestParam(required = false) String sort,
                                                @RequestParam(defaultValue = "0") int offset,
                                                @RequestParam(defaultValue = "0") int limit,
                                                @RequestParam(defaultValue = "false") boolean desc) {
        TransactionFilter filter = TransactionFilter.builder()
                .action(action)
                .cardNumber(cardNumber)
                .amountFrom(amountFrom)
                .amountTo(amountTo)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .build();
        return Flux.fromIterable(transactionService.filter(filter, sort, offset, limit, desc));
    }
}
