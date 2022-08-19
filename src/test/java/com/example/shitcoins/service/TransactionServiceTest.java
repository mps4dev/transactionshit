package com.example.shitcoins.service;

import com.example.shitcoins.domain.Account;
import com.example.shitcoins.dto.OwnerDto;
import com.example.shitcoins.dto.TransactionDto;
import com.example.shitcoins.error.NotEnoughFundsException;
import com.example.shitcoins.error.TransactionException;
import com.example.shitcoins.repository.AccountRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.shitcoins.AccountConstants.ACCOUNT_NUMBER_1;
import static com.example.shitcoins.AccountConstants.ACCOUNT_NUMBER_2;
import static com.example.shitcoins.AccountConstants.ACCOUNT_NUMBER_3;
import static com.example.shitcoins.AccountConstants.BALANCE;
import static com.example.shitcoins.AccountConstants.OWNER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionServiceTest {
    
    private static final BigDecimal AMOUNT = BigDecimal.ONE;

    private final AccountRepository repository = mock(AccountRepository.class);
    private final TransactionService testee = new TransactionService(repository);

    @Test
    void deposit_shouldNotDepositInCaseOfValidationErrors() {
        // given
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER_1, BALANCE.negate(), OWNER_ID);

        // when then
        assertThrows(TransactionException.class, () -> testee.deposit(transaction), "Amount cannot be less than zero");
        verify(repository, times(2)).findByAccountNumber(ACCOUNT_NUMBER_1);
        verify(repository, never()).save(any());
    }

    @Test
    void deposit_shouldSuccessfullyDeposit() {
        // given
        Account account = new Account(ACCOUNT_NUMBER_1, BALANCE, OWNER_ID);
        Account updatedAccount = new Account(account.getNumber(), account.getBalance().add(AMOUNT), account.getOwnerId());
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER_1, AMOUNT, OWNER_ID);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(Optional.of(account));
        when(repository.save(account)).thenReturn(updatedAccount);

        // when
        Account result = testee.deposit(transaction);
        
        // then
        assertThat(result).isEqualTo(updatedAccount);
    }

    @Test
    void withdraw_shouldThrowExceptionWhenNotEnoughFunds() {
        // given
        Account account = new Account(ACCOUNT_NUMBER_1, BALANCE, OWNER_ID);
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER_1, BigDecimal.TEN.multiply(BigDecimal.valueOf(2)), OWNER_ID);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(Optional.of(account));

        // when then
        assertThrows(NotEnoughFundsException.class, () -> testee.withdraw(transaction), "Not enough funds to withdraw 20 from account PL000000000000000000000");
    }

    @Test
    void withdraw_shouldSuccessfullyWithdraw() {
        // given
        Account account = new Account(ACCOUNT_NUMBER_1, BALANCE, OWNER_ID);
        Account updatedAccount = new Account(account.getNumber(), account.getBalance().subtract(AMOUNT), account.getOwnerId());
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER_1, AMOUNT, OWNER_ID);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(Optional.of(account));
        when(repository.save(account)).thenReturn(updatedAccount);

        // when
        Account result = testee.withdraw(transaction);
        // then
        assertThat(result).isEqualTo(updatedAccount);
    }

    @Test
    void withdraw_shouldThrowExceptionTryingToCreateAccountWithDebt() {
        // given
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER_1, AMOUNT, OWNER_ID);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(Optional.empty());

        // when then
        assertThrows(TransactionException.class, () -> testee.withdraw(transaction), "Cannot create account with a debt");
        verify(repository, times(1)).findByAccountNumber(ACCOUNT_NUMBER_1);
    }

    @Test
    void deposit_shouldAddFundsWhenTryingToCreateAccountWithExistingNumber() {
        // given
        Account account = new Account(ACCOUNT_NUMBER_1, BALANCE, OWNER_ID);
        Account savedAccount = new Account(account.getNumber(), account.getBalance().add(AMOUNT), account.getOwnerId());
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER_1, AMOUNT, OWNER_ID);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(Optional.empty()).thenReturn(Optional.of(account));
        when(repository.save(account)).thenReturn(savedAccount);

        // when then
        Account result = testee.deposit(transaction);
        assertThat(result).isEqualTo(savedAccount);
    }

    @Test
    void deposit_shouldCreateNewAccount() {
        // given
        Account newAccount = new Account(ACCOUNT_NUMBER_1, AMOUNT, OWNER_ID);
        Account savedAccount = new Account(newAccount.getNumber(), newAccount.getBalance().subtract(AMOUNT), newAccount.getOwnerId());
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER_1, AMOUNT, OWNER_ID);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(Optional.empty()).thenReturn(Optional.empty());
        when(repository.save(newAccount)).thenReturn(savedAccount);

        // when
        Account result = testee.deposit(transaction);

        // then
        assertThat(result).isEqualTo(savedAccount);
    }

    @Test
    void deposit_shouldNotCreateNewAccountWhenOwnerIsNotProvided() {
        // given
        TransactionDto transaction = new TransactionDto(ACCOUNT_NUMBER_1, AMOUNT, null);
        when(repository.findByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(Optional.empty()).thenReturn(Optional.empty());

        // when then
        assertThrows(TransactionException.class, () -> testee.deposit(transaction), "No owner provided");
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
        Account account1 = new Account(ACCOUNT_NUMBER_1, BALANCE, 10);
        Account account2 = new Account(ACCOUNT_NUMBER_2, BALANCE.multiply(BigDecimal.valueOf(1.2)), 20);
        Account account3 = new Account(ACCOUNT_NUMBER_3, BALANCE.multiply(BigDecimal.valueOf(0.8)), 30);
        when(repository.findTop(3)).thenReturn(List.of(account2, account1, account3));

        // when
        List<OwnerDto> result = testee.topOwners(3);

        // then
        assertThat(result).isEqualTo(List.of(new OwnerDto(account2.getOwnerId()), new OwnerDto(account1.getOwnerId()), new OwnerDto(account3.getOwnerId())));
    }

}
