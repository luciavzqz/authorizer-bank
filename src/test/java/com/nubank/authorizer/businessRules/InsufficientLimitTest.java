package com.nubank.authorizer.businessRules;

import com.nubank.authorizer.businessRules.transactionRules.CardNotActive;
import com.nubank.authorizer.businessRules.transactionRules.InsufficientLimit;
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

public class InsufficientLimitTest {
    DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    @Test
    @DisplayName("Available limit is sufficient")
    void sufficientLimit() {
        Rule rule = new InsufficientLimit(null);

        /*
           # Input
            {"account": {"active-card": true, "available-limit": 10000}}
            {"transaction": {"merchant": "Vivara", "amount": 1250, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Samsung", "amount": 2500, "time": "2019-02-13T11:00:01.000Z"}}
            {"transaction": {"merchant": "Nike", "amount": 800, "time": "2019-02-13T11:01:01.000Z"}}
         */
        GenericTransaction a1 = new Account(true, 10000);
        GenericTransaction a2 = new Transaction("Vivara", 1250, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter));
        GenericTransaction a3 = new Transaction("Samsung", 750, LocalDateTime.parse("2019-02-13T11:00:01.000Z",formatter));
        GenericTransaction a4 = new Transaction("Nike", 800, LocalDateTime.parse("2019-02-13T11:01:01.000Z",formatter));

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(true,10000, new ArrayList<>())));
        in.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,10000, new ArrayList<>())));
        in.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,10000, new ArrayList<>())));
        in.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,10000, new ArrayList<>())));

         /*
           # Output
            {"account": {"active-card": true,"available-limit": 10000}, "violations": []}
            {"account": {"active-card": true,"available-limit": 10000}, "violations":  []}
            {"account": {"active-card": true,"available-limit": 10000}, "violations": []}
            {"account": {"active-card": true,"available-limit": 10000}, "violations": []}
         */
        List<ValidatedTransaction> out = new ArrayList<>();
        out.add(new ValidatedTransaction(a1,new AuthorizedTransaction(true, 10000, new ArrayList<>())));
        out.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,10000, new ArrayList<>())));
        out.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,10000, new ArrayList<>())));
        out.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,10000, new ArrayList<>())));

        assertEquals(
                rule.runValidator(in),
                out,
                "It is not identified that the available limit is sufficient");
    }

    @Test
    @DisplayName("CardIsNotActive")
    void cardNotActive() {
        Rule rule = new InsufficientLimit(null);

        /*
           # Input
            {"account": {"active-card": false, "available-limit": 10000}}
            {"transaction": {"merchant": "Vivara", "amount": 1250, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Samsung", "amount": 2500, "time": "2019-02-13T11:00:01.000Z"}}
            {"transaction": {"merchant": "Nike", "amount": 800, "time": "2019-02-13T11:01:01.000Z"}}
         */
        GenericTransaction a1 = new Account(false, 10000);
        GenericTransaction a2 = new Transaction("Vivara", 1250, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter));
        GenericTransaction a3 = new Transaction("Samsung", 750, LocalDateTime.parse("2019-02-13T11:00:01.000Z",formatter));
        GenericTransaction a4 = new Transaction("Nike", 800, LocalDateTime.parse("2019-02-13T11:01:01.000Z",formatter));

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(false,10000, new ArrayList<>())));
        in.add(new ValidatedTransaction(a2, new AuthorizedTransaction(false,10000, new ArrayList<>())));
        in.add(new ValidatedTransaction(a3, new AuthorizedTransaction(false,10000, new ArrayList<>())));
        in.add(new ValidatedTransaction(a4, new AuthorizedTransaction(false,10000, new ArrayList<>())));

         /*
           # Output
            {"account": {"active-card": false,"available-limit": 10000}, "violations": []}
            {"account": {"active-card": false,"available-limit": 10000}, "violations":  []}
            {"account": {"active-card": false,"available-limit": 10000}, "violations": []}
            {"account": {"active-card": false,"available-limit": 10000}, "violations": []}
         */
        List<ValidatedTransaction> out = new ArrayList<>();
        out.add(new ValidatedTransaction(a1,new AuthorizedTransaction(false, 10000, new ArrayList<>())));
        out.add(new ValidatedTransaction(a2, new AuthorizedTransaction(false,10000, new ArrayList<>())));
        out.add(new ValidatedTransaction(a3, new AuthorizedTransaction(false,10000, new ArrayList<>())));
        out.add(new ValidatedTransaction(a4, new AuthorizedTransaction(false,10000, new ArrayList<>())));

        assertEquals(
                rule.runValidator(in),
                out,
                "It is not identified that the available limit is sufficient");
    }

    @Test
    @DisplayName("Available limit is insufficient")
    void insufficientLimit() {
        Rule rule = new InsufficientLimit(null);

        /*
           # Input
            {"account": {"active-card": true, "available-limit": 1000}}
            {"transaction": {"merchant": "Vivara", "amount": 1250, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Samsung", "amount": 2500, "time": "2019-02-13T11:00:01.000Z"}}
            {"transaction": {"merchant": "Nike", "amount": 800, "time": "2019-02-13T11:01:01.000Z"}}
         */
        GenericTransaction a1 = new Account(true, 1000);
        GenericTransaction a2 = new Transaction("Vivara", 1250, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter));
        GenericTransaction a3 = new Transaction("Samsung", 2500, LocalDateTime.parse("2019-02-13T11:00:01.000Z",formatter));
        GenericTransaction a4 = new Transaction("Nike", 800, LocalDateTime.parse("2019-02-13T11:01:01.000Z",formatter));

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(true,1000, new ArrayList<>())));
        in.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,1000, new ArrayList<>())));
        in.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,1000, new ArrayList<>())));
        in.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,1000, new ArrayList<>())));

         /*
           # Output
            {"account": {"active-card": true,"available-limit": 1000}, "violations": []}
            {"account": {"active-card": true,"available-limit": 1000}, "violations":  ["insufficient-limit"]}
            {"account": {"active-card": true,"available-limit": 1000}, "violations": ["insufficient-limit"]}
            {"account": {"active-card": true,"available-limit": 1000}, "violations": []}
         */
        List<ValidatedTransaction> out = new ArrayList<>();

        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.INSUFFICIENT_LIMIT.getValidation());

        out.add(new ValidatedTransaction(a1,new AuthorizedTransaction(true, 1000, new ArrayList<>())));
        out.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,1000, violations)));
        out.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,1000, violations)));
        out.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,1000, new ArrayList<>())));

        assertEquals(
                rule.runValidator(in),
                out,
                "It is not identified that the available limit is insufficient");
    }
}
