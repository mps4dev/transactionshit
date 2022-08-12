package com.example.shitcoins.repository;

import com.example.shitcoins.domain.Account;
import com.example.shitcoins.error.RecordNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.shitcoins.AccountConstants.ACCOUNT_NUMBER;
import static com.example.shitcoins.AccountConstants.BALANCE;
import static com.example.shitcoins.AccountConstants.OWNER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryAccountRepositoryTest {

    private InMemoryAccountRepository testee;

    @BeforeEach
    void setUp() {
        testee = new InMemoryAccountRepository();
    }

    @Test
    void save_shouldSaveNewAccount() {
        // given
        Account account = new Account(ACCOUNT_NUMBER, BALANCE, OWNER_ID);

        // when
        Account result = testee.save(account);

        // then
        assertThat(result).isEqualTo(new Account(1, ACCOUNT_NUMBER, BALANCE, OWNER_ID));
    }

    @Test
    void save_shouldSaveExistingAccountAccount() {
        // given
        Account account = new Account(ACCOUNT_NUMBER, BALANCE, OWNER_ID);
        Account savedAccount = testee.save(account);
        Account newAccount = new Account(savedAccount.getId(), ACCOUNT_NUMBER, BALANCE.add(BigDecimal.ONE), OWNER_ID);

        // when
        Account result = testee.save(newAccount);

        // then
        assertThat(result).isEqualTo(new Account(savedAccount.getId(), ACCOUNT_NUMBER, BigDecimal.valueOf(11.0), OWNER_ID));
    }

    @Test
    void update_shouldThrowExceptionWhenThereIsNoExistingAccount() {
        // given
        Account account = new Account(ACCOUNT_NUMBER, BALANCE, OWNER_ID);

        // when then
        assertThrows(RecordNotFoundException.class, () -> testee.update(1, BigDecimal.TEN),"Account with id=1 not found");
    }

    @Test
    void update_shouldUpdateSuccessfully() {
        // given
        Account account = new Account(ACCOUNT_NUMBER, BALANCE, OWNER_ID);
        Account savedAccount = testee.save(account);
        BigDecimal newBalance = BigDecimal.TEN.multiply(BigDecimal.valueOf(2));

        // when
        Account result = testee.update(savedAccount.getId(), newBalance);

        // then
        assertThat(result).isEqualTo(new Account(savedAccount.getId(), ACCOUNT_NUMBER, newBalance, OWNER_ID));
    }

    @Test
    void findByAccountNumber_shouldNotFindAccountByNumberWhenAccountDoesNotExist() {
        assertThat(testee.findByAccountNumber(ACCOUNT_NUMBER)).isEmpty();
    }

    @Test
    void findByAccountNumber_shouldFindAccountByNumberWhenAccountExists() {
        // given
        Account account = new Account(ACCOUNT_NUMBER, BALANCE, OWNER_ID);
        testee.save(account);

        // when
        Optional<Account> result = testee.findByAccountNumber(ACCOUNT_NUMBER);

        // then
        assertThat(result).contains(new Account(1, ACCOUNT_NUMBER, BALANCE, OWNER_ID));
    }

    @Test
    void findTop_shouldFindTopNAccountsWhenThereIsNoAccount() {
        assertThat(testee.findTop(1)).isEmpty();
    }

    @Test
    void findTop_shouldFindTopNAccountsWhenThereAreLessAccountThanWanted() {
        // given
        Account account1 = new Account(ACCOUNT_NUMBER, BALANCE, OWNER_ID);
        Account savedAccount1 =  testee.save(account1);
        Account account2 = new Account(ACCOUNT_NUMBER, BALANCE.multiply(BigDecimal.valueOf(2)), OWNER_ID + 1);
        Account savedAccount2 = testee.save(account2);

        // when
        List<Account> result = testee.findTop(3);

        // then
        assertThat(result).isEqualTo(List.of(savedAccount2, savedAccount1));
    }

    @Test
    void findTop_shouldFindTopNAccountsWhenThereAreMoreAccountThanWanted() {
        // given
        Account account1 = new Account(ACCOUNT_NUMBER, BALANCE, OWNER_ID);
        Account savedAccount1 = testee.save(account1);
        Account account2 = new Account(ACCOUNT_NUMBER, BALANCE.multiply(BigDecimal.valueOf(2)), OWNER_ID + 1);
        Account savedAccount2 = testee.save(account2);
        Account account3 = new Account(ACCOUNT_NUMBER, BALANCE.multiply(BigDecimal.valueOf(0.7)), OWNER_ID + 2);
        testee.save(account3);

        // when
        List<Account> result = testee.findTop(2);

        // then
        assertThat(result).isEqualTo(List.of(savedAccount2, savedAccount1));
    }

    @Test
    void findTop_shouldReturnEmptyListWhenZeroWanted() {
        // given
        Account account = new Account(ACCOUNT_NUMBER, BALANCE, OWNER_ID);
        testee.save(account);

        // when
        List<Account> result = testee.findTop(0);

        // then
        assertThat(result).isEmpty();
    }

}
