package com.zilch.card.service;

import com.zilch.card.dto.CardDTO;

import java.math.BigDecimal;
import java.util.List;

public interface CardService {
    CardDTO findById(Long id);

    CardDTO saveCard(CardDTO cardDTO);

    List<CardDTO> getAll();

    CardDTO deleteCard(Long id);

    CardDTO credit(Long id, BigDecimal amount);

    CardDTO debit(Long id, BigDecimal amount);
}
