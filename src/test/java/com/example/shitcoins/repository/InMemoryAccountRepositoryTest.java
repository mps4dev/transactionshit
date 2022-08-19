package com.example.shitcoins.repository;

import com.example.shitcoins.domain.Account;
import org.junit.jupiter.api.BeforeEach;
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

class InMemoryAccountRepositoryTest {

    private InMemoryAccountRepository testee;

    private Account account;

    @BeforeEach
    void setUp() {
        testee = new InMemoryAccountRepository();
        account = new Account(ACCOUNT_NUMBER_1, BALANCE, OWNER_ID);
    }

    @Test
    void save_shouldSaveNewAccount() {
        // given when
        Account result = testee.save(account);

        // then
        assertThat(result).isEqualTo(new Account(ACCOUNT_NUMBER_1, BALANCE, OWNER_ID));
    }

    @Test
    void save_shouldSaveExistingAccountAccount() {
        // given
        Account savedAccount = testee.save(account);
        Account newAccount = new Account(ACCOUNT_NUMBER_1, BALANCE.add(BigDecimal.ONE), OWNER_ID);

        // when
        Account result = testee.save(newAccount);

        // then
        assertThat(result).isEqualTo(new Account(ACCOUNT_NUMBER_1, BigDecimal.valueOf(11), OWNER_ID));
    }

    @Test
    void findByAccountNumber_shouldNotFindAccountByNumberWhenAccountDoesNotExist() {
        assertThat(testee.findByAccountNumber(ACCOUNT_NUMBER_1)).isEmpty();
    }

    @Test
    void findByAccountNumber_shouldFindAccountByNumberWhenAccountExists() {
        // given
        testee.save(account);

        // when
        Optional<Account> result = testee.findByAccountNumber(ACCOUNT_NUMBER_1);

        // then
        assertThat(result).contains(new Account(ACCOUNT_NUMBER_1, BALANCE, OWNER_ID));
    }

    @Test
    void findTop_shouldFindTopNAccountsWhenThereIsNoAccount() {
        assertThat(testee.findTop(1)).isEmpty();
    }

    @Test
    void findTop_shouldFindTopNAccountsWhenThereAreLessAccountThanWanted() {
        // given
        Account account1 = new Account(ACCOUNT_NUMBER_1, BALANCE, OWNER_ID);
        Account savedAccount1 =  testee.save(account1);
        Account account2 = new Account(ACCOUNT_NUMBER_2, BALANCE.multiply(BigDecimal.valueOf(2)), OWNER_ID + 1);
        Account savedAccount2 = testee.save(account2);

        // when
        List<Account> result = testee.findTop(3);

        // then
        assertThat(result).isEqualTo(List.of(savedAccount2, savedAccount1));
    }

    @Test
    void findTop_shouldFindTopNAccountsWhenThereAreMoreAccountThanWanted() {
        // given
        Account account1 = new Account(ACCOUNT_NUMBER_1, BALANCE, OWNER_ID);
        Account savedAccount1 = testee.save(account1);
        Account account2 = new Account(ACCOUNT_NUMBER_2, BALANCE.multiply(BigDecimal.valueOf(2)), OWNER_ID + 1);
        Account savedAccount2 = testee.save(account2);
        Account account3 = new Account(ACCOUNT_NUMBER_3, BALANCE.multiply(BigDecimal.valueOf(0.7)), OWNER_ID + 2);
        testee.save(account3);

        // when
        List<Account> result = testee.findTop(2);

        // then
        assertThat(result).isEqualTo(List.of(savedAccount2, savedAccount1));
    }

    @Test
    void findTop_shouldReturnEmptyListWhenZeroWanted() {
        // given
        testee.save(account);

        // when
        List<Account> result = testee.findTop(0);

        // then
        assertThat(result).isEmpty();
    }

}
