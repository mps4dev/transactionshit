package com.example.shitcoins.domain;

import com.example.shitcoins.dto.AccountDto;
import com.example.shitcoins.error.NotEnoughFundsException;
import com.example.shitcoins.error.TransactionException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account {

    private final String number;
    private BigDecimal balance;
    private final int ownerId;

    public Account(String number, BigDecimal balance, int ownerId) {
        validateAmount(balance);
        this.number = number;
        this.balance = balance;
        this.ownerId = ownerId;
    }

    public Account add(BigDecimal amount) {
        validateAmount(amount);
        this.balance = this.balance.add(amount);
        return this;
    }

    public Account subtract(BigDecimal amount) {
        validateAmount(amount);
        if (amount.compareTo(balance) > 0) {
            throw new NotEnoughFundsException(this, amount);
        }
        this.balance = this.balance.subtract(amount);
        return this;
    }

    public AccountDto toDto() {
        return new AccountDto(this.getNumber(), this.getBalance(), this.getOwnerId());
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new TransactionException("Amount cannot be less than zero");
        }
    }
}
