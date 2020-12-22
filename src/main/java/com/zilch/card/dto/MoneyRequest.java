package com.zilch.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
public final class MoneyRequest {

    @NotNull
    @Min(1)
    private final BigDecimal amount;

    public MoneyRequest(@JsonProperty("amount") BigDecimal amount) {
        this.amount = amount;
    }
}
