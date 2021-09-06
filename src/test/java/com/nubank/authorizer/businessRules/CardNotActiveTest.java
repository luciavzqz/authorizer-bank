package com.nubank.authorizer.businessRules;

import com.nubank.authorizer.businessRules.transactionRules.CardNotActive;
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

public class CardNotActiveTest {

    DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    @Test
    @DisplayName("Card is active")
    void cardActive() {
        Rule rule = new CardNotActive(null);

        /*
           # Input
            {"account": {"active-card": true, "available-limit": 100}}
            {"transaction": {"merchant": "Burger King", "amount": 20, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Habbib's", "amount": 15, "time": "2019-02- 13T11:15:00.000Z"}}
         */
        GenericTransaction a1 = new Account(true, 100);
        GenericTransaction a2 = new Transaction("Burger King", 20, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter));
        GenericTransaction a3 = new Transaction("Habbib's", 15, LocalDateTime.parse("2019-02-13T11:15:00.000Z",formatter));

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,100, new ArrayList<>())));

         /*
           # Output
            {"account": {"active-card": false, "available-limit": 100}, "violations": []}
            {"account": {"active-card": false, "available-limit": 100}, "violations": []}
            {"account": {"active-card": false, "available-limit": 100}, "violations": []}
         */
        List<ValidatedTransaction> out = new ArrayList<>();

        out.add(new ValidatedTransaction(a1,new AuthorizedTransaction(true, 100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a2, new AuthorizedTransaction(true,100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a3, new AuthorizedTransaction(true,100, new ArrayList<>())));

        assertEquals(
                rule.runValidator(in),
                out,
                "It is not identified that the card is active");
    }

    @Test
    @DisplayName("Card is not active")
    void cardNotActive() {
        Rule rule = new CardNotActive(null);

        /*
           # Input
            {"account": {"active-card": false, "available-limit": 100}}
            {"transaction": {"merchant": "Burger King", "amount": 20, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Habbib's", "amount": 15, "time": "2019-02- 13T11:15:00.000Z"}}
         */
        GenericTransaction a1 = new Account(false, 100);
        GenericTransaction a2 = new Transaction("Burger King", 20, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter));
        GenericTransaction a3 = new Transaction("Habbib's", 15, LocalDateTime.parse("2019-02-13T11:15:00.000Z",formatter));

        List<ValidatedTransaction> in = new ArrayList<>();
        in.add(new ValidatedTransaction(a1, new AuthorizedTransaction(false,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a2, new AuthorizedTransaction(false,100, new ArrayList<>())));
        in.add(new ValidatedTransaction(a3, new AuthorizedTransaction(false,100, new ArrayList<>())));

         /*
           # Output
            {"account": {"active-card": false, "available-limit": 100}, "violations": []}
            {"account": {"active-card": false, "available-limit": 100}, "violations": ["card-not-active"]}
            {"account": {"active-card": false, "available-limit": 100}, "violations": ["card-not-active"]}
         */
        List<ValidatedTransaction> out = new ArrayList<>();

        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.CARD_NOT_ACTIVE.getValidation());

        out.add(new ValidatedTransaction(a1,new AuthorizedTransaction(false, 100, new ArrayList<>())));
        out.add(new ValidatedTransaction(a2, new AuthorizedTransaction(false,100, violations)));
        out.add(new ValidatedTransaction(a3, new AuthorizedTransaction(false,100, violations)));

        assertEquals(
                rule.runValidator(in),
                out,
                "It is not identified that the card is not active");
    }
}
