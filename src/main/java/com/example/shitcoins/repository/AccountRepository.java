package com.example.shitcoins.repository;

import com.example.shitcoins.domain.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Account update(int accountId, BigDecimal amount);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findTop(int n);

}
