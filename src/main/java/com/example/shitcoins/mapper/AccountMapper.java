package com.example.shitcoins.mapper;

import com.example.shitcoins.dto.AccountDto;
import com.example.shitcoins.domain.Account;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class AccountMapper {

    public AccountDto map(@NonNull Account account) {
        return new AccountDto(account.getNumber(), account.getBalance(), account.getOwnerId());
    }
}
