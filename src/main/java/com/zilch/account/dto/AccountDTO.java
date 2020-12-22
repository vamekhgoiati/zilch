package com.zilch.account.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.zilch.account.entity.Account;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = AccountDTO.Builder.class)
public class AccountDTO {

    Long id;
    @NotBlank
    String firstName;
    @NotBlank
    String lastName;

    public static AccountDTO toDto(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .build();
    }

    public static Account fromDto(AccountDTO accountDTO) {
        Account account = new Account();
        account.setId(accountDTO.getId());
        account.setFirstName(accountDTO.getFirstName());
        account.setLastName(accountDTO.getLastName());
        return account;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
    }
}
