package com.example.shitcoins.dto;

import com.example.shitcoins.domain.TransactionType;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

public record TransactionDto(@NonNull String accountNumber, @NonNull BigDecimal amount, @Nullable Integer ownerId,
                             @NonNull TransactionType type) {

}
