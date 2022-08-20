package com.example.shitcoins.service;

import com.example.shitcoins.dto.TransactionDto;
import com.example.shitcoins.repository.AccountRepository;
import com.example.shitcoins.repository.InMemoryAccountRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.shitcoins.AccountConstants.ACCOUNT_NUMBER_1;
import static com.example.shitcoins.AccountConstants.BALANCE;
import static com.example.shitcoins.AccountConstants.OWNER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MultithreadTransactionServiceTest {

    private final AccountRepository repository = new InMemoryAccountRepository();
    private final TransactionService testee = new TransactionService(repository);

    @Test
    void testConcurrentDeposit() throws InterruptedException {
        int numberOfThreads = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            submitDeposit(executorService, countDownLatch);
        }
        countDownLatch.await();
        assertEquals(BigDecimal.valueOf(numberOfThreads), repository.findByAccountNumber(ACCOUNT_NUMBER_1).get().getBalance());
    }

    @Test
    void testConcurrentWithdrawal() throws InterruptedException {
        testee.deposit(new TransactionDto(ACCOUNT_NUMBER_1, BALANCE, OWNER_ID));
        int numberOfThreads = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            submitWithdrawal(executorService, countDownLatch);
        }
        countDownLatch.await();
        assertEquals(BALANCE.subtract(BigDecimal.valueOf(numberOfThreads)), repository.findByAccountNumber(ACCOUNT_NUMBER_1).get().getBalance());
    }

    @Test
    void testConcurrentDepositsAndWithdrawals() throws InterruptedException {
        testee.deposit(new TransactionDto(ACCOUNT_NUMBER_1, BALANCE, OWNER_ID));
        int numberOfThreads = 9;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            if (i % 2 == 0) {
                submitDeposit(executorService, countDownLatch);
            } else {
                submitWithdrawal(executorService, countDownLatch);
            }
        }
        countDownLatch.await();
        assertEquals(BALANCE.add(BigDecimal.valueOf(Math.ceil(numberOfThreads / 2.0))).subtract(BigDecimal.valueOf(Math.floor(numberOfThreads / 2.0))).setScale(0), repository.findByAccountNumber(ACCOUNT_NUMBER_1).get().getBalance());
    }

    private void submitDeposit(ExecutorService executorService, CountDownLatch countDownLatch) {
        executorService.submit(() -> {
            try {
                testee.deposit(new TransactionDto(ACCOUNT_NUMBER_1, BigDecimal.ONE, OWNER_ID));
            } finally {
                countDownLatch.countDown();
            }

        });
    }

    private void submitWithdrawal(ExecutorService executorService, CountDownLatch countDownLatch) {
        executorService.submit(() -> {
            try {
                testee.withdraw(new TransactionDto(ACCOUNT_NUMBER_1, BigDecimal.ONE, OWNER_ID));
            } finally {
                countDownLatch.countDown();
            }

        });
    }
}
