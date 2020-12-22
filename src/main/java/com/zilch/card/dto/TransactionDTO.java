package com.zilch.card.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zilch.card.action.CardAction;
import com.zilch.card.entity.Transaction;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder
public class TransactionDTO {
    Long id;
    CardAction action;
    String cardNumber;
    BigDecimal amount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    Instant transactionDate;

    public static TransactionDTO toDto(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .action(transaction.getAction())
                .cardNumber(transaction.getCard().getCardNumber())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}
