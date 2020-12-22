package com.zilch.account.service;

import com.zilch.account.dto.AccountDTO;

import java.util.List;

public interface AccountService {
    AccountDTO findById(Long id);

    AccountDTO save(AccountDTO accountDTO);

    List<AccountDTO> getAll();

    AccountDTO deleteAccount(Long id);
}
