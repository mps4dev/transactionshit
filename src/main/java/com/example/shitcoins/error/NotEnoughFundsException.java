package com.example.shitcoins.error;

import com.example.shitcoins.dto.TransactionDto;

public class NotEnoughFundsException extends RuntimeException {

    public NotEnoughFundsException(TransactionDto transaction) {
        super(String.format("Not enough funds to %s %s from account %s", transaction.type().name().toLowerCase(), transaction.amount(), transaction.accountNumber()));
    }
}
