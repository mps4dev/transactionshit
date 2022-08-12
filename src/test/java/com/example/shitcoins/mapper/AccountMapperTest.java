package com.example.shitcoins.mapper;

import com.example.shitcoins.domain.Account;
import com.example.shitcoins.dto.AccountDto;
import org.junit.jupiter.api.Test;

import static com.example.shitcoins.AccountConstants.ACCOUNT_NUMBER;
import static com.example.shitcoins.AccountConstants.BALANCE;
import static com.example.shitcoins.AccountConstants.OWNER_ID;
import static org.assertj.core.api.Assertions.assertThat;

class AccountMapperTest {

    private final AccountMapper testee = new AccountMapper();

    @Test
    void shouldMap() {
        // given
        Account account = new Account(1, ACCOUNT_NUMBER, BALANCE, OWNER_ID);

        // when
        AccountDto result = testee.map(account);

        // then
        assertThat(result).isEqualTo(new AccountDto(ACCOUNT_NUMBER, BALANCE, OWNER_ID));
    }
}
