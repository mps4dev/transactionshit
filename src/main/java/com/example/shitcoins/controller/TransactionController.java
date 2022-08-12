package com.example.shitcoins.controller;

import com.example.shitcoins.dto.OwnerDto;
import com.example.shitcoins.domain.TransactionType;
import com.example.shitcoins.dto.AccountDto;
import com.example.shitcoins.dto.TransactionDto;
import com.example.shitcoins.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @PutMapping("/deposit")
    public AccountDto deposit(@RequestBody AccountDto account) {
        return service.doTransaction(new TransactionDto(account.accountNumber(), account.amount(), account.ownerId(), TransactionType.DEPOSIT));
    }

    @PutMapping("/withdrawal")
    public AccountDto withdraw(@RequestBody AccountDto account) {
        return service.doTransaction(new TransactionDto(account.accountNumber(), account.amount(), account.ownerId(), TransactionType.WITHDRAW));
    }

    @GetMapping("/owners")
    public List<OwnerDto> topOwners(@PathParam("size") int limit) {
        return service.topOwners(limit);
    }
}
