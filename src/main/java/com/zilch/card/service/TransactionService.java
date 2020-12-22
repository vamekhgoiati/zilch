package com.zilch.card.service;

import com.zilch.card.dto.TransactionDTO;
import com.zilch.card.dto.TransactionFilter;

import java.util.List;

public interface TransactionService {

    List<TransactionDTO> filter(TransactionFilter filter, String sort, Integer offset, Integer limit, boolean desc);

    TransactionDTO findById(Long id);
}
