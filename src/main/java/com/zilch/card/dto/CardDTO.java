package com.zilch.card.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.zilch.card.entity.Card;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = CardDTO.Builder.class)
public class CardDTO {
    Long id;
    @NotBlank
    String cardNumber;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    Instant creationDate;
    BigDecimal balance;
    @NotNull
    Long accountId;

    public static CardDTO toDto(Card card) {
        return CardDTO.builder()
                .id(card.getId())
                .cardNumber(card.getCardNumber())
                .creationDate(card.getCreationDate())
                .balance(card.getBalance())
                .accountId(card.getAccount().getId())
                .build();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
    }
}
