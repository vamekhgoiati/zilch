package com.zilch.account.service.impl;

import com.zilch.account.dto.AccountDTO;
import com.zilch.account.entity.Account;
import com.zilch.account.repository.AccountRepository;
import com.zilch.account.service.AccountService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDTO findById(Long id) {
        return accountRepository.findById(id).map(AccountDTO::toDto).orElseThrow();
    }

    @Override
    public AccountDTO save(AccountDTO accountDTO) {
        Account account = accountRepository.save(AccountDTO.fromDto(accountDTO));
        return AccountDTO.toDto(account);
    }

    @Override
    public List<AccountDTO> getAll() {
        return StreamSupport.stream(accountRepository.findAll().spliterator(), false)
                .map(AccountDTO::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDTO deleteAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow();
        accountRepository.deleteById(id);
        return AccountDTO.toDto(account);
    }
}
