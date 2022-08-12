package com.example.shitcoins.dto;

import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;


public record AccountDto(@NonNull String accountNumber, @NonNull BigDecimal amount, @Nullable Integer ownerId) {

}
