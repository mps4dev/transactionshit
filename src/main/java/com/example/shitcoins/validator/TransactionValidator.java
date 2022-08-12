package com.example.shitcoins.validator;

import com.example.shitcoins.domain.TransactionType;
import com.example.shitcoins.dto.TransactionDto;
import com.example.shitcoins.error.TransactionException;

import java.math.BigDecimal;

public class TransactionValidator {

    private TransactionValidator() {}

    public static void validateTransaction(TransactionDto transaction) {
        if (transaction.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new TransactionException("Amount cannot be less than zero");
        }
    }

    public static void validateTransactionDataToCreate(TransactionDto transaction) {
        if (transaction.ownerId() == null) {
            throw new TransactionException("No owner provided");
        }
        if (transaction.type() == TransactionType.WITHDRAW) {
            throw new TransactionException("Cannot create account with a debt");
        }
    }
}
