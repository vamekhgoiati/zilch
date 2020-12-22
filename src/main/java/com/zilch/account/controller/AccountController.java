package com.zilch.account.controller;

import com.zilch.account.dto.AccountDTO;
import com.zilch.account.service.AccountService;
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
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("{id}")
    public Mono<AccountDTO> getAccount(@PathVariable Long id) {
        return Mono.just(accountService.findById(id));
    }

    @GetMapping
    public Flux<AccountDTO> getAccounts() {
        return Flux.fromIterable(accountService.getAll());
    }

    @PostMapping
    public Mono<AccountDTO> saveAccount(@Valid @RequestBody AccountDTO accountDTO) {
        return Mono.just(accountService.save(accountDTO));
    }

    @DeleteMapping("{id}")
    public Mono<AccountDTO> deleteAccount(@PathVariable Long id) {
        return Mono.just(accountService.deleteAccount(id));
    }
}
