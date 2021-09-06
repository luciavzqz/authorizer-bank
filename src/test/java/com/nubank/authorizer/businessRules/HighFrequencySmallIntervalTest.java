package com.nubank.authorizer.businessRules;

import com.nubank.authorizer.businessRules.transactionRules.HighFrequencySmallInterval;
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

public class HighFrequencySmallIntervalTest {
    DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    @Test
    @DisplayName("Two high frequency in a small interval")
    void twoHighFrequencySmallInterval() {
        Rule rule = new HighFrequencySmallInterval(null);

        /*
           # Input
            {"account": {"active-card": true, "available-limit": 100}}
            {"transaction": {"merchant": "Burger King", "amount": 20, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Habbib's", "amount": 20, "time": "2019-02-13T11:00:01.000Z"}}
            {"transaction": {"merchant": "McDonald's", "amount": 20, "time": "2019-02-13T11:01:01.000Z"}}
            {"transaction": {"merchant": "Subway", "amount": 20, "time": "2019-02-13T11:01:31.000Z"}}
            {"transaction": {"merchant": "Burger King", "amount": 10, "time": "2019-02-13T11:01:33.000Z"}}
            {"transaction": {"merchant": "Burger King", "amount": 10, "time": "2019-02-13T12:00:00.000Z"}}
         */
        GenericTransaction a1 = new Account(true, 100);
        GenericTransaction a2 = new Transaction("Burger King", 20, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter));
        GenericTransaction a3 = new Transaction("Habbib's", 20, LocalDateTime.parse("2019-02-13T11:00:01.000Z",formatter));
        GenericTransaction a4 = new Transaction("McDonald's", 20, LocalDateTime.parse("2019-02-13T11:01:01.000Z",formatter));
        GenericTransaction a5 = new Transaction("Subway", 20, LocalDateTime.parse("2019-02-13T11:01:31.000Z",formatter));
        GenericTransaction a6 = new Transaction("Burger King", 10, LocalDateTime.parse("2019-02-13T11:01:33.000Z",formatter));
        GenericTransaction a7 = new Transaction("Burger King", 10, LocalDateTime.parse("2019-02-13T12:00:00.000Z",formatter));

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a5, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a6, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a7, new AuthorizedTransaction(true,100, new ArrayList<>())));

         /*
           # Output
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": ["high-frequency-small-interval"]}
            {"account": {"active-card": true, "available-limit": 100}, "violations": ["high-frequency-small-interval"]}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
         */
        List<ValidatedTransaction> out = new ArrayList<>();

        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.HIGH_FREQUENCY_SMALL_INTERVAL.getValidation());

        out.add(new ValidatedTransaction(a1,new AuthorizedTransaction(true, 100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a5, new AuthorizedTransaction(true,100, violations)));
        out.add(new ValidatedTransaction(a6, new AuthorizedTransaction(true,100, violations)));
        out.add(new ValidatedTransaction(a7, new AuthorizedTransaction(true,100, new ArrayList<>())));

        assertEquals(
                rule.runValidator(in),
                out,
                "It is not identified that there are 2 transaction with high frequency in a small interval");
    }

    @Test
    @DisplayName("High frecuency in a small interval")
    void highFrecuencySmallInterval() {
        Rule rule = new HighFrequencySmallInterval(null);

        /*
           # Input
            {"account": {"active-card": true, "available-limit": 100}}
            {"transaction": {"merchant": "Burger King", "amount": 20, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Habbib's", "amount": 20, "time": "2019-02-13T11:00:01.000Z"}}
            {"transaction": {"merchant": "McDonald's", "amount": 20, "time": "2019-02-13T11:01:01.000Z"}}
            {"transaction": {"merchant": "Subway", "amount": 20, "time": "2019-02-13T11:01:31.000Z"}}
            {"transaction": {"merchant": "Burger King", "amount": 10, "time": "2019-02-13T12:00:00.000Z"}}
         */
        GenericTransaction a1 = new Account(true, 100);
        GenericTransaction a2 = new Transaction("Burger King", 20, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter));
        GenericTransaction a3 = new Transaction("Habbib's", 20, LocalDateTime.parse("2019-02-13T11:00:01.000Z",formatter));
        GenericTransaction a4 = new Transaction("McDonald's", 20, LocalDateTime.parse("2019-02-13T11:01:01.000Z",formatter));
        GenericTransaction a5 = new Transaction("Subway", 20, LocalDateTime.parse("2019-02-13T11:01:31.000Z",formatter));
        GenericTransaction a6 = new Transaction("Burger King", 10, LocalDateTime.parse("2019-02-13T12:00:00.000Z",formatter));

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a5, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a6, new AuthorizedTransaction(true,100, new ArrayList<>())));

         /*
           # Output
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 100}, "violations": ["high-frequency-small-interval"]}
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
         */
        List<ValidatedTransaction> out = new ArrayList<>();

        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.HIGH_FREQUENCY_SMALL_INTERVAL.getValidation());

        out.add(new ValidatedTransaction(a1,new AuthorizedTransaction(true, 100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a4, new AuthorizedTransaction(true,100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a5, new AuthorizedTransaction(true,100, violations)));
        out.add(new ValidatedTransaction(a6, new AuthorizedTransaction(true,100, new ArrayList<>())));

        assertEquals(
                rule.runValidator(in),
                out,
                "It is not identified that there is high frecuency in a small interval");
    }

}
