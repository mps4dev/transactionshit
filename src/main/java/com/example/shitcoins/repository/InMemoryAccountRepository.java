package com.example.shitcoins.repository;

import com.example.shitcoins.domain.Account;
import com.example.shitcoins.document.AccountDocument;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<Integer, AccountDocument> accounts = new ConcurrentHashMap<>();
    private final AtomicInteger lastId = new AtomicInteger(1);

    @Override
    public synchronized Account save(Account account) {
        int id = find(account.getNumber())
                .map(AccountDocument::getId)
                .orElseGet(this::getNextId);
        return accounts.compute(id, (accId, acc) -> AccountDocument.fromAccount(id, account)).toAccount();
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return find(accountNumber).map(AccountDocument::toAccount);
    }

    @Override
    public List<Account> findTop(int limit) {
        return accounts.values()
                .stream()
                .sorted(Comparator.comparing(AccountDocument::getBalance).reversed())
                .limit(limit)
                .map(AccountDocument::toAccount)
                .toList();
    }

    private int getNextId() {
        return lastId.getAndIncrement();
    }

    private Optional<AccountDocument> find(String accountNumber) {
        return accounts.values().stream().filter(account -> account.getNumber().equals(accountNumber)).findFirst();
    }

}
