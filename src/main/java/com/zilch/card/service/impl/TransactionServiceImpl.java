package com.zilch.card.service.impl;

import com.zilch.card.dao.TransactionDao;
import com.zilch.card.dto.TransactionDTO;
import com.zilch.card.dto.TransactionFilter;
import com.zilch.card.service.TransactionService;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionServiceImpl implements TransactionService {

    private static final String DEFAULT_SORT = "transactionDate";
    private static final int DEFAULT_LIMIT = 50;

    private final TransactionDao transactionDao;

    public TransactionServiceImpl(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    @Override
    public List<TransactionDTO> filter(TransactionFilter filter, String sort, Integer offset, Integer limit, boolean desc) {
        if (!StringUtils.hasText(sort)) {
            sort = DEFAULT_SORT;
        }

        if (limit == 0) {
            limit = DEFAULT_LIMIT;
        }

        return transactionDao.filter(filter, sort, offset, limit, desc)
                .stream()
                .map(TransactionDTO::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDTO findById(Long id) {
        return transactionDao.findById(id).map(TransactionDTO::toDto).orElseThrow();
    }
}
