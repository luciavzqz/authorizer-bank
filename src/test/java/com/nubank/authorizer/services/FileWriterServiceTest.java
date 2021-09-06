package com.nubank.authorizer.services;

import com.nubank.authorizer.entities.Account;
import com.nubank.authorizer.entities.AuthorizedTransaction;
import com.nubank.authorizer.entities.Transaction;
import com.nubank.authorizer.entities.ValidatedTransaction;
import com.nubank.authorizer.enums.RuleValidator;
import com.nubank.authorizer.interfaces.GenericTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileWriterServiceTest {

    @Test
    @DisplayName("Simple authorized transaction")
    void simpleAuthorizedTransaction() {
        AuthorizedTransaction authorizedTransaction = new AuthorizedTransaction(true,100,new ArrayList<>());
         assertEquals(
                authorizedTransaction.toString(),
                "{\"account\": {\"active-card\": true, \"available-limit\": 100}, \"violations\": []}",
                "toString method doesn't work");
    }

    @Test
    @DisplayName("Account not initialized")
    void accountNotInitialized() {
        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.ACCOUNT_NOT_INITIALIZED.getValidation());
        AuthorizedTransaction authorizedTransaction = new AuthorizedTransaction(null,null,violations);
        assertEquals(
                authorizedTransaction.toString(),
                "{\"account\": {}, \"violations\": [account-not-initialized]}",
                "toString method doesn't work");
    }

    @Test
    @DisplayName("Authorized transactions with violations")
    void authorizedTransactionWithViolations() {
        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.DOUBLE_TRANSACTION.getValidation());
        violations.add(RuleValidator.HIGH_FREQUENCY_SMALL_INTERVAL.getValidation());
        AuthorizedTransaction authorizedTransaction = new AuthorizedTransaction(true,100,violations);
        assertEquals(
                authorizedTransaction.toString(),
                "{\"account\": {\"active-card\": true, \"available-limit\": 100}, \"violations\": [double-transaction, high-frequency-small-interval]}",
                "toString method doesn't work");
    }
}
