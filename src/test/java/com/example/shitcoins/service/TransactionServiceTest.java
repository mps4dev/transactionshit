package com.example.shitcoins.service;


import com.example.shitcoins.domain.Account;
import com.example.shitcoins.domain.TransactionType;
import com.example.shitcoins.dto.AccountDto;
import com.example.shitcoins.dto.OwnerDto;
import com.example.shitcoins.dto.TransactionDto;
import com.example.shitcoins.error.NotEnoughFundsException;
import com.example.shitcoins.error.TransactionException;
import com.example.shitcoins.mapper.AccountMapper;
import com.example.shitcoins.repository.AccountRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.shitcoins.AccountConstants.ACCOUNT_NUMBER;
import static com.example.shitcoins.AccountConstants.BALANCE;
import static com.example.shitcoins.AccountConstants.OWNER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TransactionServiceTest {
    
    private static final BigDecimal AMOUNT = BigDecimal.ONE;
    private static final int ACCOUNT_ID = 1;

    private final AccountRepository repository = mock(AccountRepository.class);
    private final AccountMapper mapper = mock(AccountMapper.class);
    private final TransactionService testee = new TransactionService(repository, mapper);;

    @Test
    void doTransaction_shouldNotDoTransactionInCaseOfValidationErrors() {
        // given
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER, BALANCE.negate(), OWNER_ID, TransactionType.DEPOSIT);

        // when then
        assertThrows(TransactionException.class, () -> testee.doTransaction(transaction), "Amount cannot be less than zero");
        verifyNoInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void doTransaction_shouldSuccessfullyDeposit() {
        // given
        Account account = new Account(ACCOUNT_ID, ACCOUNT_NUMBER, BALANCE, OWNER_ID);
        Account updatedAccount = new Account(account.getId(), account.getNumber(), account.getBalance().add(AMOUNT), account.getOwnerId());
        AccountDto updatedAccountDto = new AccountDto(updatedAccount.getNumber(), updatedAccount.getBalance(), updatedAccount.getOwnerId());
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER, AMOUNT, OWNER_ID, TransactionType.DEPOSIT);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));
        when(repository.update(ACCOUNT_ID, AMOUNT)).thenReturn(updatedAccount);
        when(mapper.map(updatedAccount)).thenReturn(updatedAccountDto);

        // when
        AccountDto result = testee.doTransaction(transaction);
        
        // then
        assertThat(result).isEqualTo(updatedAccountDto);
    }

    @Test
    void doTransaction_shouldThrowExceptionWhenNotEnoughFunds() {
        // given
        Account account = new Account(ACCOUNT_ID, ACCOUNT_NUMBER, BALANCE, OWNER_ID);
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER, BigDecimal.TEN.multiply(BigDecimal.valueOf(2)), OWNER_ID, TransactionType.WITHDRAW);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));

        // when then
        assertThrows(NotEnoughFundsException.class, () -> testee.doTransaction(transaction), "Not enough funds to withdraw 20 from account PL000000000000000000000");
        verifyNoInteractions(mapper);
    }

    @Test
    void doTransaction_shouldSuccessfullyWithdraw() {
        // given
        Account account = new Account(ACCOUNT_ID, ACCOUNT_NUMBER, BALANCE, OWNER_ID);
        Account updatedAccount = new Account(account.getId(), account.getNumber(), account.getBalance().subtract(AMOUNT), account.getOwnerId());
        AccountDto updatedAccountDto = new AccountDto(updatedAccount.getNumber(), updatedAccount.getBalance(), updatedAccount.getOwnerId());
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER, AMOUNT, OWNER_ID, TransactionType.WITHDRAW);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(account));
        when(repository.update(ACCOUNT_ID, AMOUNT.negate())).thenReturn(updatedAccount);
        when(mapper.map(updatedAccount)).thenReturn(updatedAccountDto);

        // when
        AccountDto result = testee.doTransaction(transaction);
        // then
        assertThat(result).isEqualTo(updatedAccountDto);
    }

    @Test
    void doTransaction_shouldThrowExceptionTryingToCreateAccountWithDebt() {
        // given
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER, AMOUNT, OWNER_ID, TransactionType.WITHDRAW);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        // when then
        assertThrows(TransactionException.class, () -> testee.doTransaction(transaction), "Cannot create account with a debt");
        verifyNoInteractions(mapper);
    }

    @Test
    void doTransaction_shouldThrowExceptionWhenTryingToCreateAccountWithExistingNumber() {
        // given
        Account account = new Account(ACCOUNT_ID, ACCOUNT_NUMBER, BALANCE, OWNER_ID);
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER, AMOUNT, OWNER_ID, TransactionType.DEPOSIT);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty()).thenReturn(Optional.of(account));

        // when then
        assertThrows(TransactionException.class, () -> testee.doTransaction(transaction), "Account with number PL000000000000000000000 already exists");
        verify(repository, times(2)).findByAccountNumber(ACCOUNT_NUMBER);
    }

    @Test
    void doTransaction_shouldCreateNewAccount() {
        // given
        Account newAccount = new Account(ACCOUNT_NUMBER, BALANCE, OWNER_ID);
        Account savedAccount = new Account(ACCOUNT_ID, newAccount.getNumber(), newAccount.getBalance().subtract(AMOUNT), newAccount.getOwnerId());
        AccountDto savedAccountDto = new AccountDto(savedAccount.getNumber(), savedAccount.getBalance(), savedAccount.getOwnerId());
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER, AMOUNT, OWNER_ID, TransactionType.DEPOSIT);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty()).thenReturn(Optional.empty());
        when(repository.save(newAccount)).thenReturn(savedAccount);
        when(mapper.map(savedAccount)).thenReturn(savedAccountDto);

        // when
        AccountDto result = testee.doTransaction(transaction);

        // then
        assertThat(result).isEqualTo(savedAccountDto);
    }

    @Test
    void doTransaction_shouldNotCreateNewAccountWhenOwnerIsNotProvided() {
        // given
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER, AMOUNT, null, TransactionType.DEPOSIT);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty()).thenReturn(Optional.empty());

        // when then
        assertThrows(TransactionException.class, () -> testee.doTransaction(transaction), "No owner provided");
    }

    @Test
    void topOwners_shouldReturnEmptyListOfTopOwnersWhenNoAccounts() {
        // given
        when(repository.findTop(0)).thenReturn(List.of());

        // when
        List<OwnerDto> result = testee.topOwners(3);

        // then
        assertThat(result).isEmpty();
    }


    @Test
    void topOwners_shouldThrowExceptionWhenNegativeIsWanted() {
        assertThrows(IllegalArgumentException.class, () -> testee.topOwners(-1),"Limit cannot be less than zero");
    }

    @Test
    void topOwners_shouldReturnSuccessfullyTopOwners() {
        // given
        Account account1 = new Account(1, ACCOUNT_NUMBER, BALANCE, 10);
        Account account2 = new Account(2, ACCOUNT_NUMBER, BALANCE.multiply(BigDecimal.valueOf(1.2)), 20);
        Account account3 = new Account(3, ACCOUNT_NUMBER, BALANCE.multiply(BigDecimal.valueOf(0.8)), 30);
        when(repository.findTop(3)).thenReturn(List.of(account2, account1, account3));

        // when
        List<OwnerDto> result = testee.topOwners(3);

        // then
        assertThat(result).isEqualTo(List.of(new OwnerDto(account2.getOwnerId()), new OwnerDto(account1.getOwnerId()), new OwnerDto(account3.getOwnerId())));
    }

}
