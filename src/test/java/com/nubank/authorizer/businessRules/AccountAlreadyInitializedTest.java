package com.nubank.authorizer.businessRules;

import com.nubank.authorizer.businessRules.accountRules.AccountAlreadyInitialized;
import com.nubank.authorizer.entities.Account;
import com.nubank.authorizer.entities.AuthorizedTransaction;
import com.nubank.authorizer.entities.Transaction;
import com.nubank.authorizer.entities.ValidatedTransaction;
import com.nubank.authorizer.enums.RuleValidator;
import com.nubank.authorizer.interfaces.GenericTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountAlreadyInitializedTest {

    DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    @Test
    @DisplayName("Simple account is initialized correctly")
    void simpleAccountInitializedCorrectly() {
        BusinessRule businessRule = new AccountAlreadyInitialized(null);
        GenericTransaction a1 = new Account(false, 750);

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(null,null, new ArrayList<>())));

        List<ValidatedTransaction> out = new ArrayList<>();
        out.add(new ValidatedTransaction(a1, new AuthorizedTransaction(false,750, new ArrayList<>())));

        assertEquals( businessRule.runValidator(in), out,
                "Account is not initialized correctly");
    }

    @Test
    @DisplayName("Account is initialized correctly")
    void accountInitializedCorrectly() {
        BusinessRule businessRule = new AccountAlreadyInitialized(null);
        GenericTransaction a1 = new Account(false, 750);

        GenericTransaction a2 = new Transaction("Gaga", 750, LocalDateTime.parse("2019-02-13T10:00:00.000Z",formatter));

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(null,null, new ArrayList<>())));
        in.add(new ValidatedTransaction(a2, new AuthorizedTransaction(null,null, new ArrayList<>())));

        List<ValidatedTransaction> out = new ArrayList<>();
        out.add(new ValidatedTransaction(a1, new AuthorizedTransaction(false,750, new ArrayList<>())));
        out.add(new ValidatedTransaction(a2, new AuthorizedTransaction(false,750, new ArrayList<>())));

        assertEquals( businessRule.runValidator(in), out,
                "Account is not initialized correctly");
    }

    @Test
    @DisplayName("Transaction without account")
    void transactionWithoutAccount() {
        BusinessRule businessRule = new AccountAlreadyInitialized(null);

        GenericTransaction a1 = new Transaction("Uber Eats", 750, LocalDateTime.parse("2020-12-01T11:07:00.000Z",formatter));
        GenericTransaction a2 = new Account(true, 750);
        GenericTransaction a3 = new Transaction("Uber Eats", 750, LocalDateTime.parse("2020-12-01T11:07:00.000Z",formatter));

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(null,null, new ArrayList<>())));
        in.add(new ValidatedTransaction(a2, new AuthorizedTransaction(null,null, new ArrayList<>())));
        in.add(new ValidatedTransaction(a3, new AuthorizedTransaction(null,null, new ArrayList<>())));

        List<ValidatedTransaction> out = new ArrayList<>();

        out.add(new ValidatedTransaction(a1,new AuthorizedTransaction(null, null, new ArrayList<>())));
        out.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,750, new ArrayList<>())));
        out.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,750, new ArrayList<>())));

        assertEquals(
                businessRule.runValidator(in),
                out,
                "It is not identified that the account is not initialized");
    }

    @Test
    @DisplayName("Account is already initialized")
    void accountAlreadyInitialized() {
        BusinessRule businessRule = new AccountAlreadyInitialized(null);
        GenericTransaction a1 = new Account(true, 175);
        GenericTransaction a2 = new Account(true, 175);

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(null,null, new ArrayList<>())));
        in.add(new ValidatedTransaction(a2, new AuthorizedTransaction(null,null, new ArrayList<>())));

        List<ValidatedTransaction> out = new ArrayList<>();
        out.add(new ValidatedTransaction(a1, new AuthorizedTransaction(true,175, new ArrayList<>())));

        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.ACCOUNT_ALREADY_INITIALIZED.getValidation());
        out.add(new ValidatedTransaction(a2,new AuthorizedTransaction(true, 175, violations)));

        assertEquals(
                businessRule.runValidator(in),
                out,
                "It is not identified that the account is already initialized");
    }
}
