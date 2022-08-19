package com.example.shitcoins.error;

import com.example.shitcoins.domain.Account;

import java.math.BigDecimal;

public class NotEnoughFundsException extends RuntimeException {

    public NotEnoughFundsException(Account account, BigDecimal amount) {
        super(String.format("Not enough funds to withdraw %s from account %s", amount, account.getNumber()));
    }
}
