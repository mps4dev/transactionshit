package com.example.shitcoins.repository;

import com.example.shitcoins.domain.Account;
import com.example.shitcoins.error.RecordNotFoundException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;

@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<Integer, Account> accounts = new HashMap<>();
    private final AtomicInteger lastId = new AtomicInteger(1);

    @Override
    public Account save(Account account) {
        Account accountToSave = account;
        if (account.getId() == 0) {
            accountToSave = new Account(getNextId(), account.getNumber(), account.getBalance(), account.getOwnerId());
        }
        accounts.put(accountToSave.getId(), accountToSave);
        return accountToSave;
    }

    @Override
    public Account update(int accountId, BigDecimal amount) {
        Account accountToBeLocked = accounts.get(accountId);
        if (isNull(accountToBeLocked)) throw new RecordNotFoundException(accountId);

        synchronized(accountToBeLocked) {
            accounts.compute(accountId, (accId, acc) -> acc.add(amount));
        }

        return accountToBeLocked;
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return accounts.values().stream().filter(account -> account.getNumber().equals(accountNumber)).findFirst();
    }

    @Override
    public List<Account> findTop(int limit) {
        return accounts.values()
                .stream()
                .sorted(Comparator.comparing(Account::getBalance).reversed())
                .limit(limit)
                .toList();
    }

    private int getNextId() {
        return lastId.getAndIncrement();
    }

}
