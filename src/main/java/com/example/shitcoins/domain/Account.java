package com.example.shitcoins.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Account {

    private final int id;
    private final String number;
    private BigDecimal balance;
    private final int ownerId;

    public Account(String number, BigDecimal balance, int ownerId) {
        this.id = 0;
        this.number = number;
        this.balance = balance;
        this.ownerId = ownerId;
    }

    public Account add(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
