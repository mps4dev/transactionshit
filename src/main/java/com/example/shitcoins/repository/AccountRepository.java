package com.example.shitcoins.repository;

import com.example.shitcoins.domain.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findTop(int limit);

}
