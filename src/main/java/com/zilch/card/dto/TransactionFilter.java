package com.zilch.card.dto;


import com.zilch.card.action.CardAction;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder(builderClassName = "Builder", toBuilder = true)
public class TransactionFilter {
    CardAction action;
    String cardNumber;
    BigDecimal amountFrom;
    BigDecimal amountTo;
    Instant dateFrom;
    Instant dateTo;
}
