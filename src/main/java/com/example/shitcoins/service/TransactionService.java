package com.example.shitcoins.service;

import com.example.shitcoins.domain.Account;
import com.example.shitcoins.dto.OwnerDto;
import com.example.shitcoins.dto.TransactionDto;
import com.example.shitcoins.error.TransactionException;
import com.example.shitcoins.repository.AccountRepository;
import com.example.shitcoins.transaction.TransactionLocks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final AccountRepository repository;

    public Account deposit(TransactionDto transaction) {
       return TransactionLocks.doInLock(transaction.accountNumber(), () -> depositInternal(transaction));
    }

    public Account withdraw(TransactionDto transaction) {
        return TransactionLocks.doInLock(transaction.accountNumber(), () -> withdrawInternal(transaction));
    }

    public List<OwnerDto> topOwners(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Limit cannot be less than zero");
        }
        return repository.findTop(limit)
                .stream()
                .map(Account::getOwnerId)
                .map(OwnerDto::new)
                .toList();
    }

    private Account depositInternal(TransactionDto transaction) {
        return repository.findByAccountNumber(transaction.accountNumber())
                .map(account -> updateAccount(account, transaction, Account::add))
                .orElseGet(() -> createNewAccount(transaction));
    }

    private Account withdrawInternal(TransactionDto transaction) {
        return repository.findByAccountNumber(transaction.accountNumber())
                .map(account -> updateAccount(account, transaction, Account::subtract))
                .orElseThrow(() -> new TransactionException("Cannot create account with a debt"));
    }

    private synchronized Account updateAccount(Account account, TransactionDto transaction, BiFunction<Account, BigDecimal, Account> updater) {
        return repository.save(updater.apply(account, transaction.amount()));
    }

    private Account createNewAccount(TransactionDto transaction) {
        if (transaction.ownerId() == null) {
            throw new TransactionException("No owner provided");
        }
        return repository.findByAccountNumber(transaction.accountNumber())
                .map(account -> repository.save(account.add(transaction.amount())))
                .orElseGet(() -> repository.save(new Account(transaction.accountNumber(), transaction.amount(), transaction.ownerId())));
    }
}
