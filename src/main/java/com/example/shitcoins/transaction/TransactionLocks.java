package com.example.shitcoins.transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionLocks {

    private TransactionLocks() {}

    private static final Map<String, ReentrantLock> LOCKS = new ConcurrentHashMap<>();

    public static void lockAccount(String accountNumber) {
        LOCKS.computeIfAbsent(accountNumber, accNumber -> new ReentrantLock()).lock();
    }

    public static void unlockAccount(String accountNumber) {
        ReentrantLock lock = LOCKS.get(accountNumber);
        if (lock != null) {
            lock.unlock();
        }
    }

}
