package com.example.shitcoins.document;

import com.example.shitcoins.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class AccountDocument {

    private final int id;
    private final String number;
    private BigDecimal balance;
    private final int ownerId;

    public Account toAccount() {
        return new Account(number, balance, ownerId);
    }

    public static AccountDocument fromAccount(int accountId, Account account) {
        return new AccountDocument(accountId, account.getNumber(), account.getBalance(), account.getOwnerId());
    }
}
