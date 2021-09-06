package com.nubank.authorizer.businessRules;

import com.nubank.authorizer.businessRules.transactionRules.DoubleTransaction;
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

public class DoubleTransactionTest {

    DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    @Test
    @DisplayName("There isn't double transaction")
    void notDoubleTransactions() {
        Rule rule = new DoubleTransaction(null);

        /*
           # Input
            {"account": {"active-card": true, "available-limit": 100}}
            {"transaction": {"merchant": "Nike", "amount": 20, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Nike", "amount": 120, "time": "2019-02-13T11:00:01.000Z"}}
            {"transaction": {"merchant": "Nike", "amount": 220, "time": "2019-02-13T11:00:02.000Z"}}
            {"transaction": {"merchant": "Nike", "amount": 15, "time": "2019-02-13T11:00:03.000Z"}}
         */
        GenericTransaction a1 = new Account(true, 100);
        GenericTransaction a2 = new Transaction("Nike", 20, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter));
        GenericTransaction a3 = new Transaction("Nike", 120, LocalDateTime.parse("2019-02-13T11:00:01.000Z",formatter));
        GenericTransaction a4 = new Transaction("Nike", 220, LocalDateTime.parse("2019-02-13T11:00:02.000Z",formatter));
        GenericTransaction a5 = new Transaction("Nike", 15, LocalDateTime.parse("2019-02-13T11:00:03.000Z",formatter));

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a5, new AuthorizedTransaction(true,100, new ArrayList<>())));

         /*
           # Output
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
         */
        List<ValidatedTransaction> out = new ArrayList<>();

        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.DOUBLE_TRANSACTION.getValidation());

        out.add(new ValidatedTransaction(a1,new AuthorizedTransaction(true, 100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a5, new AuthorizedTransaction(true,100, new ArrayList<>())));

        assertEquals(
                rule.runValidator(in),
                out,
                "It is not identified that there is not double transaction");
    }

    @Test
    @DisplayName("There is double transactions")
    void doubleTransactions() {
        Rule rule = new DoubleTransaction(null);

        /*
           # Input
            {"account": {"active-card": true, "available-limit": 100}}
            {"transaction": {"merchant": "Nike", "amount": 20, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Nike", "amount": 20, "time": "2019-02-13T11:00:01.000Z"}}
            {"transaction": {"merchant": "Nike", "amount": 20, "time": "2019-02-13T11:00:02.000Z"}}
            {"transaction": {"merchant": "Nike", "amount": 15, "time": "2019-02-13T11:00:03.000Z"}}
         */
        GenericTransaction a1 = new Account(true, 100);
        GenericTransaction a2 = new Transaction("Nike", 20, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter));
        GenericTransaction a3 = new Transaction("Nike", 20, LocalDateTime.parse("2019-02-13T11:00:01.000Z",formatter));
        GenericTransaction a4 = new Transaction("Nike", 20, LocalDateTime.parse("2019-02-13T11:00:02.000Z",formatter));
        GenericTransaction a5 = new Transaction("Nike", 15, LocalDateTime.parse("2019-02-13T11:00:03.000Z",formatter));

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a5, new AuthorizedTransaction(true,100, new ArrayList<>())));

         /*
           # Output
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": ["doubled-transaction"]}
            {"account": {"active-card": true, "available-limit": 100}, "violations": ["doubled-transaction"]}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
         */
        List<ValidatedTransaction> out = new ArrayList<>();

        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.DOUBLE_TRANSACTION.getValidation());

        out.add(new ValidatedTransaction(a1,new AuthorizedTransaction(true, 100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,100, violations)));
        out.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,100, violations)));
        out.add(new ValidatedTransaction(a5, new AuthorizedTransaction(true,100, new ArrayList<>())));

        assertEquals(
                rule.runValidator(in),
                out,
                "It is not identified that there are double transactions");
    }

    @Test
    @DisplayName("There is double transaction")
    void doubleTransaction() {
        Rule rule = new DoubleTransaction(null);

        /*
           # Input
            {"account": {"active-card": true, "available-limit": 100}}
            {"transaction": {"merchant": "Nike", "amount": 20, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Samsung", "amount": 10, "time": "2019-02-13T11:00:01.000Z"}}
            {"transaction": {"merchant": "Nike", "amount": 20, "time": "2019-02-13T11:00:02.000Z"}}
            {"transaction": {"merchant": "Nike", "amount": 15, "time": "2019-02-13T11:00:03.000Z"}}
         */
        GenericTransaction a1 = new Account(true, 100);
        GenericTransaction a2 = new Transaction("Nike", 20, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter));
        GenericTransaction a3 = new Transaction("Samsung", 10, LocalDateTime.parse("2019-02-13T11:00:01.000Z",formatter));
        GenericTransaction a4 = new Transaction("Nike", 20, LocalDateTime.parse("2019-02-13T11:00:02.000Z",formatter));
        GenericTransaction a5 = new Transaction("Nike", 15, LocalDateTime.parse("2019-02-13T11:00:03.000Z",formatter));

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a5, new AuthorizedTransaction(true,100, new ArrayList<>())));

         /*
           # Output
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": ["doubled-transaction"]}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
         */
        List<ValidatedTransaction> out = new ArrayList<>();

        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.DOUBLE_TRANSACTION.getValidation());

        out.add(new ValidatedTransaction(a1,new AuthorizedTransaction(true, 100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,100, violations)));
        out.add(new ValidatedTransaction(a5, new AuthorizedTransaction(true,100, new ArrayList<>())));

        assertEquals(
                rule.runValidator(in),
                out,
                "It is not identified that there is double transaction");
    }
}
