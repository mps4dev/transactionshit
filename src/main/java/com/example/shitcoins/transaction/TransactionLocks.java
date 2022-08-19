package com.example.shitcoins.transaction;

import com.example.shitcoins.domain.Account;
import com.example.shitcoins.error.TransactionException;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionLocks {

    private TransactionLocks() {}

    private static final Map<String, ReentrantLock> LOCKS = new ConcurrentHashMap<>();

    public static Account doInLock(String accountNumber, Callable<Account> callable) {
        ReentrantLock lock = LOCKS.computeIfAbsent(accountNumber, accNumber -> new ReentrantLock());
        try {
            lock.lock();
            return callable.call();
        } catch (Exception e) {
            throw new TransactionException(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

}
