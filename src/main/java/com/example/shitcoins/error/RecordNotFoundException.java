package com.example.shitcoins.error;

public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException(int id) {
        super(String.format("Account with id=%d not found", id));
    }
}
