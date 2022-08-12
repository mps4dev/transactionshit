package com.example.shitcoins.service;

import com.example.shitcoins.domain.Account;
import com.example.shitcoins.dto.OwnerDto;
import com.example.shitcoins.dto.AccountDto;
import com.example.shitcoins.dto.TransactionDto;
import com.example.shitcoins.error.NotEnoughFundsException;
import com.example.shitcoins.error.TransactionException;
import com.example.shitcoins.mapper.AccountMapper;
import com.example.shitcoins.repository.AccountRepository;
import com.example.shitcoins.validator.TransactionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.shitcoins.domain.TransactionType.DEPOSIT;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final AccountRepository repository;
    private final AccountMapper accountMapper;

    public AccountDto doTransaction(TransactionDto transaction) {
        TransactionValidator.validateTransaction(transaction);
        return repository.findByAccountNumber(transaction.accountNumber())
                .map(account -> updateAccount(transaction, account))
                .orElseGet(() -> createNewAccount(transaction));
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

    private synchronized AccountDto createNewAccount(TransactionDto transaction) {
        TransactionValidator.validateTransactionDataToCreate(transaction);
        Optional<Account> maybeAccount = repository.findByAccountNumber(transaction.accountNumber());
        if (maybeAccount.isPresent()) {
            throw new TransactionException(String.format("Account with number %s already exists", transaction.accountNumber()));
        } else {
            Account newAccount = new Account(transaction.accountNumber(), transaction.amount(), transaction.ownerId());
            newAccount = repository.save(newAccount);
            return accountMapper.map(newAccount);
        }
    }

    private AccountDto updateAccount(TransactionDto transaction, Account account) {
        if (account.getBalance().compareTo(transaction.amount()) < 0) {
            throw new NotEnoughFundsException(transaction);
        }
        BigDecimal amountToChange = transaction.type() == DEPOSIT ? transaction.amount() : transaction.amount().negate();
        Account updatedAccount = repository.update(account.getId(), amountToChange);
        return accountMapper.map(updatedAccount);
    }
}
