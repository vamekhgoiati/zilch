package com.zilch.account;

import com.zilch.account.repository.AccountRepository;
import com.zilch.account.service.AccountService;
import com.zilch.account.service.impl.AccountServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableWebFlux
public class AccountConfiguration {

    @Bean
    public AccountService accountService(AccountRepository accountRepository) {
        return new AccountServiceImpl(accountRepository);
    }
}
