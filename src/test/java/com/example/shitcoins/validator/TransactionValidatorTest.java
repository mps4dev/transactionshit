package com.example.shitcoins.validator;

import com.example.shitcoins.domain.TransactionType;
import com.example.shitcoins.dto.TransactionDto;
import com.example.shitcoins.error.TransactionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.shitcoins.AccountConstants.ACCOUNT_NUMBER;
import static com.example.shitcoins.AccountConstants.AMOUNT;
import static com.example.shitcoins.AccountConstants.OWNER_ID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionValidatorTest {

    @Test
    void validateTransaction_shouldThrowExceptionWhenAmountIsNegative () {
        // given
        TransactionDto transactionDto = new TransactionDto(ACCOUNT_NUMBER, AMOUNT.negate(), OWNER_ID, TransactionType.DEPOSIT);

        // when then
        assertThrows(TransactionException.class, () -> TransactionValidator.validateTransaction(transactionDto), "Amount cannot be less than zero");
    }

    @Test
    void validateTransaction_shouldNotThrowExceptionWhenAmountIsZero () {
        // given
        TransactionDto transactionDto = new TransactionDto(ACCOUNT_NUMBER, BigDecimal.ZERO, OWNER_ID, TransactionType.DEPOSIT);

        // when then
        assertDoesNotThrow(() -> TransactionValidator.validateTransaction(transactionDto));
    }

    @Test
    void validateTransaction_shouldNotThrowExceptionWhenAmountIsPositive () {
        // given
        TransactionDto transactionDto = new TransactionDto(ACCOUNT_NUMBER, AMOUNT, OWNER_ID, TransactionType.DEPOSIT);

        // when then
        assertDoesNotThrow(() -> TransactionValidator.validateTransaction(transactionDto));
    }

    @Test
    void validateTransactionDataToCreate_shouldThrowExceptionWhenNoOwnerProvided () {
        // given
        TransactionDto transactionDto = new TransactionDto(ACCOUNT_NUMBER, AMOUNT, null, TransactionType.DEPOSIT);

        // when then
        assertThrows(TransactionException.class, () -> TransactionValidator.validateTransactionDataToCreate(transactionDto), "No owner provided");
    }

    @Test
    void validateTransactionDataToCreate_shouldThrowExceptionWhenWithdrawal () {
        // given
        TransactionDto transactionDto = new TransactionDto(ACCOUNT_NUMBER, AMOUNT, OWNER_ID, TransactionType.WITHDRAW);

        // when then
        assertThrows(TransactionException.class, () -> TransactionValidator.validateTransactionDataToCreate(transactionDto), "Cannot create account with a debt");
    }

    @Test
    void validateTransactionDataToCreate_shouldNoThrowException () {
        // given
        TransactionDto transactionDto = new TransactionDto(ACCOUNT_NUMBER, AMOUNT, OWNER_ID, TransactionType.DEPOSIT);

        // when then
        assertDoesNotThrow(() -> TransactionValidator.validateTransactionDataToCreate(transactionDto));
    }
}
