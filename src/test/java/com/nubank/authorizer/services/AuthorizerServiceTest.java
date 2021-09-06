package com.nubank.authorizer.services;

import com.nubank.authorizer.entities.Account;
import com.nubank.authorizer.entities.AuthorizedTransaction;
import com.nubank.authorizer.entities.Transaction;
import com.nubank.authorizer.enums.RuleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthorizerServiceTest {

    AuthorizerService authorizerService;
    DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        authorizerService = new AuthorizerService();
    }

    @Test
    @DisplayName("Challenge example")
    void challengeExample() {
        /*
           # Input
            {"account": {"active-card": true, "available-limit": 100}}
            {"transaction": {"merchant": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z"}}
            {"transaction": {"merchant": "Habbib's", "amount": 90, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "McDonald's", "amount": 30, "time": "2019-02-13T12:00:00.000Z"}}
         */
        List<Object> in = new ArrayList<>();
        in.add(new Account(true, 100));
        in.add(new Transaction("Burger King", 20, LocalDateTime.parse("2019-02-13T10:00:00.000Z",formatter)));
        in.add(new Transaction("Habbib's", 90, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter)));
        in.add(new Transaction("McDonald's", 30, LocalDateTime.parse("2019-02-13T12:00:00.000Z",formatter)));

         /*
           # Output
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 80}, "violations": []}
            {"account": {"active-card": true, "available-limit": 80}, "violations": ["insufficient-limit"]}
            {"account": {"active-card": true, "available-limit": 50}, "violations": []}
         */
        List<AuthorizedTransaction> out = new ArrayList<>();
        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.INSUFFICIENT_LIMIT.getValidation());

        out.add(new AuthorizedTransaction(true, 100, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,80, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,80, violations));
        out.add(new AuthorizedTransaction(true,50, new ArrayList<>()));

        assertEquals(
                authorizerService.authorize(in),
                out);
    }

    @Test
    @DisplayName("Creating an account successfully")
    void creatingAnAccountSuccessfully() {
        /*
           # Input
            {"account": {"active-card": false, "available-limit": 750}}
         */
        List<Object> in = new ArrayList<>();
        in.add(new Account(false, 750));

         /*
           # Output
            {"account": {"active-card": false, "available-limit": 750}, "violations": []}
         */
        List<AuthorizedTransaction> out = new ArrayList<>();

        out.add(new AuthorizedTransaction(false, 750, new ArrayList<>()));

        assertEquals(
                authorizerService.authorize(in),
                out);
    }

    @Test
    @DisplayName("Account violates authorizer logic")
    void accountViolatesAuthorizerLogic() {
         /*
           # Input
            {"account": {"active-card": true, "available-limit": 175}}
            {"account": {"active-card": true, "available-limit": 350}}
         */
        List<Object> in = new ArrayList<>();
        in.add(new Account(true, 175));
        in.add(new Account(true, 350));

         /*
           # Output
            {"account": {"active-card": true, "available-limit": 175}, "violations": []}
            {"account": {"active-card": true, "available-limit": 175}, "violations": ["account-already-initialized"]}
         */
        List<AuthorizedTransaction> out = new ArrayList<>();
        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.ACCOUNT_ALREADY_INITIALIZED.getValidation());

        out.add(new AuthorizedTransaction(true, 175, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true, 175, violations));

        assertEquals(
                authorizerService.authorize(in),
                out);
    }

    @Test
    @DisplayName("Processing a transaction successfully")
    void transactionSuccessfully() {
        /*
           # Input
            {"account": {"active-card": true, "available-limit": 100}}
            {"transaction": {"merchant": "Burger King", "amount": 20, "time": "2019-02-13T11:00:00.000Z"}}
         */
        List<Object> in = new ArrayList<>();
        in.add(new Account(true, 100));
        in.add(new Transaction("Burger King", 20, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter)));

         /*
           # Output
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 80}, "violations": []}
         */
        List<AuthorizedTransaction> out = new ArrayList<>();

        out.add(new AuthorizedTransaction(true, 100, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,80, new ArrayList<>()));

        assertEquals(
                authorizerService.authorize(in),
                out);
    }

    @Test
    @DisplayName("Transaction which violates the account not initialized logic")
    void violatesAccountNotInitialized() {
        /*
           # Input
            {"transaction": {"merchant": "Uber Eats", "amount": 25, "time": "2020-12-01T11:07:00.000Z"}}
            {"account": {"active-card": true, "available-limit": 225}}
            {"transaction": {"merchant": "Uber Eats", "amount": 25, "time": "2020-12-01T11:07:00.000Z"}}
         */
        List<Object> in = new ArrayList<>();
        in.add(new Transaction("Uber Eats", 25, LocalDateTime.parse("2020-12-01T11:07:00.000Z",formatter)));
        in.add(new Account(true, 225));
        in.add(new Transaction("Uber Eats", 25, LocalDateTime.parse("2020-12-01T11:07:00.000Z",formatter)));

         /*
           # Output
            {"account": {}, "violations": ["account-not-initialized"]}
            {"account": {"active-card": true, "available-limit": 225}, "violations": []}
            {"account": {"active-card": true, "available-limit": 200}, "violations": []}
         */
        List<AuthorizedTransaction> out = new ArrayList<>();
        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.ACCOUNT_NOT_INITIALIZED.getValidation());

        out.add(new AuthorizedTransaction(null,null, violations));
        out.add(new AuthorizedTransaction(true, 225, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,200, new ArrayList<>()));

        assertEquals(
                authorizerService.authorize(in),
                out);
    }

    @Test
    @DisplayName("Transaction which violates card not active logic")
    void violatesCardNotActive() {
        /*
           # Input
            {"account": {"active-card": false, "available-limit": 100}}
            {"transaction": {"merchant": "Burger King", "amount": 20, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Habbib's", "amount": 15, "time": "2019-02-13T11:15:00.000Z"}}

         */
        List<Object> in = new ArrayList<>();
        in.add(new Account(false, 100));
        in.add(new Transaction("Burger King", 20, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter)));
        in.add(new Transaction("Habbib's", 15, LocalDateTime.parse("2019-02-13T11:15:00.000Z",formatter)));

         /*
           # Output
             {"account": {"active-card": false, "available-limit": 100}, "violations": []}
            {"account": {"active-card": false, "available-limit": 100}, "violations": ["card-not-active"]}
            {"account": {"active-card": false, "available-limit": 100}, "violations": ["card-not-active"]}
         */
        List<AuthorizedTransaction> out = new ArrayList<>();
        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.CARD_NOT_ACTIVE.getValidation());

        out.add(new AuthorizedTransaction(false, 100, new ArrayList<>()));
        out.add(new AuthorizedTransaction(false,100, violations));
        out.add(new AuthorizedTransaction(false,100, violations));

        assertEquals(
                authorizerService.authorize(in),
                out);
    }

    @Test
    @DisplayName("Transaction which violates insufficient-limit logic")
    void violatesInsufficientLimit() {
        /*
           # Input
            {"account": {"active-card": true, "available-limit": 1000}}
            {"transaction": {"merchant": "Vivara", "amount": 1250, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Samsung", "amount": 2500, "time": "2019-02-13T11:00:01.000Z"}}
            {"transaction": {"merchant": "Nike", "amount": 800, "time": "2019-02-13T11:01:01.000Z"}}
         */
        List<Object> in = new ArrayList<>();
        in.add(new Account(true, 1000));
        in.add(new Transaction("Vivara", 1250, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter)));
        in.add(new Transaction("Samsung", 2500, LocalDateTime.parse("2019-02-13T11:00:01.000Z",formatter)));
        in.add(new Transaction("Nike", 800, LocalDateTime.parse("2019-02-13T11:01:01.000Z",formatter)));

         /*
           # Output
            {"account": {"active-card": true,"available-limit": 1000}, "violations": []}
            {"account": {"active-card": true,"available-limit": 1000}, "violations": ["insufficient-limit"]}
            {"account": {"active-card": true,"available-limit": 1000}, "violations": ["insufficient-limit"]}
            {"account": {"active-card": true,"available-limit": 200}, "violations": []}
         */
        List<AuthorizedTransaction> out = new ArrayList<>();
        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.INSUFFICIENT_LIMIT.getValidation());

        out.add(new AuthorizedTransaction(true, 1000, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,1000, violations));
        out.add(new AuthorizedTransaction(true,1000, violations));
        out.add(new AuthorizedTransaction(true,200, new ArrayList<>()));

        assertEquals(
                authorizerService.authorize(in),
                out);
    }

    @Test
    @DisplayName("Transaction which violates the high-frequency-small-interval logic")
    void violatesHighFrequencySmallInterval() {
        /*
           # Input
            {"account": {"active-card": true, "available-limit": 100}}
            {"transaction": {"merchant": "Burger King", "amount": 20, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Habbib's", "amount": 20, "time": "2019-02-13T11:00:01.000Z"}}
            {"transaction": {"merchant": "McDonald's", "amount": 20, "time": "2019-02-13T11:01:01.000Z"}}
            {"transaction": {"merchant": "Subway", "amount": 20, "time": "2019-02-13T11:01:31.000Z"}}
            {"transaction": {"merchant": "Burger King", "amount": 10, "time": "2019-02-13T12:00:00.000Z"}}
         */
        List<Object> in = new ArrayList<>();
        in.add(new Account(true, 100));
        in.add(new Transaction("Burger King", 20, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter)));
        in.add(new Transaction("Habbib's", 20, LocalDateTime.parse("2019-02-13T11:00:01.000Z",formatter)));
        in.add(new Transaction("McDonald's", 20, LocalDateTime.parse("2019-02-13T11:01:01.000Z",formatter)));
        in.add(new Transaction("Subway", 20, LocalDateTime.parse("2019-02-13T11:01:31.000Z",formatter)));
        in.add(new Transaction("Burger King", 10, LocalDateTime.parse("2019-02-13T12:00:00.000Z",formatter)));

         /*
           # Output
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 80}, "violations": []}
            {"account": {"active-card": true, "available-limit": 60}, "violations": []}
            {"account": {"active-card": true, "available-limit": 40}, "violations": []}
            {"account": {"active-card": true, "available-limit": 40}, "violations": ["high-frequency-small-interval"]}
            {"account": {"active-card": true, "available-limit": 30}, "violations": []}
         */
        List<AuthorizedTransaction> out = new ArrayList<>();
        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.HIGH_FREQUENCY_SMALL_INTERVAL.getValidation());

        out.add(new AuthorizedTransaction(true, 100, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,80, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,60, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,40, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,40, violations));
        out.add(new AuthorizedTransaction(true,30, new ArrayList<>()));

        assertEquals(
                authorizerService.authorize(in),
                out);
    }

    @Test
    @DisplayName("Transaction should not trigger the high-frequency-small-interval violation")
    void shouldNotTriggerHighFrequencySmallIntervalViolation() {
        /*
           # Input
            {"account": {"active-card": true, "available-limit": 1000}}
            {"transaction": {"merchant": "Vivara", "amount": 1250, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "Samsung", "amount": 2500, "time": "2019-02-13T11:00:01.000Z"}}
            {"transaction": {"merchant": "Nike", "amount": 800, "time": "2019-02-13T11:01:01.000Z"}}
            {"transaction": {"merchant": "Uber", "amount": 80, "time": "2019-02-13T11:01:31.000Z"}}
         */
        List<Object> in = new ArrayList<>();
        in.add(new Account(true, 1000));
        in.add(new Transaction("Vivara", 1250, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter)));
        in.add(new Transaction("Samsung", 2500, LocalDateTime.parse("2019-02-13T11:00:01.000Z",formatter)));
        in.add(new Transaction("Nike", 800, LocalDateTime.parse("2019-02-13T11:00:07.000Z",formatter)));
        in.add(new Transaction("Uber", 80, LocalDateTime.parse("2019-02-13T11:01:31.000Z",formatter)));

         /*
           # Output
            {"account": {"active-card": true,"available-limit": 1000}, "violations": []}
            {"account": {"active-card": true,"available-limit": 1000}, "violations": ["insufficient-limit"]}
            {"account": {"active-card": true,"available-limit": 1000}, "violations": ["insufficient-limit"]}
            {"account": {"active-card": true,"available-limit": 200}, "violations": []}
            {"account": {"active-card": true,"available-limit": 120}, "violations": []}
         */
        List<AuthorizedTransaction> out = new ArrayList<>();
        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.INSUFFICIENT_LIMIT.getValidation());

        out.add(new AuthorizedTransaction(true, 1000, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,1000, violations));
        out.add(new AuthorizedTransaction(true,1000, violations));
        out.add(new AuthorizedTransaction(true,200, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,120, new ArrayList<>()));

        assertEquals(
                authorizerService.authorize(in),
                out);
    }

    @Test
    @DisplayName("Transaction which violates the double-transaction logic")
    void violatesDoubleTransaction() {
        /*
           # Input
            {"account": {"active-card": true, "available-limit": 100}}
            {"transaction": {"merchant": "Burger King", "amount": 20, "time": "2019-02-13T11:00:00.000Z"}}
            {"transaction": {"merchant": "McDonald's", "amount": 10, "time": "2019-02-13T11:00:01.000Z"}}
            {"transaction": {"merchant": "Burger King", "amount": 20, "time": "2019-02-13T11:00:02.000Z"}}
            {"transaction": {"merchant": "Burger King", "amount": 15, "time": "2019-02-13T11:00:03.000Z"}}
         */
        List<Object> in = new ArrayList<>();
        in.add(new Account(true, 100));
        in.add(new Transaction("Burger King", 20, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter)));
        in.add(new Transaction("McDonald's", 10, LocalDateTime.parse("2019-02-13T11:00:01.000Z",formatter)));
        in.add(new Transaction("Burger King", 20, LocalDateTime.parse("2019-02-13T11:00:02.000Z",formatter)));
        in.add(new Transaction("Burger King", 15, LocalDateTime.parse("2019-02-13T11:00:03.000Z",formatter)));

         /*
           # Output
            {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            {"account": {"active-card": true, "available-limit": 80}, "violations": []}
            {"account": {"active-card": true, "available-limit": 70}, "violations": []}
            {"account": {"active-card": true, "available-limit": 70}, "violations": ["doubled-transaction"]}
            {"account": {"active-card": true, "available-limit": 55}, "violations": []}
         */
        List<AuthorizedTransaction> out = new ArrayList<>();
        List<String> violations = new ArrayList<>();
        violations.add(RuleValidator.DOUBLE_TRANSACTION.getValidation());

        out.add(new AuthorizedTransaction(true, 100, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,80, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,70, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,70, violations));
        out.add(new AuthorizedTransaction(true,55, new ArrayList<>()));

        assertEquals(
                authorizerService.authorize(in),
                out);
    }

    @Test
    @DisplayName("Transaction which violates multiple logics")
    void violatesMultipleLogics() {
        /*
           # Input
            {"account": {"active-card": true, "available-limit": 100}}
            {"transaction": {"merchant": "McDonald's", "amount": 10, "time": "2019-02-13T11:00:01.000Z"}}
            {"transaction": {"merchant": "Burger King", "amount": 20, "time": "2019-02-13T11:00:02.000Z"}}
            {"transaction": {"merchant": "Burger King", "amount": 5, "time": "2019-02-13T11:00:07.000Z"}}
            {"transaction": {"merchant": "Burger King", "amount": 5, "time": "2019-02-13T11:00:08.000Z"}}
            {"transaction": {"merchant": "Burger King", "amount": 150, "time": "2019-02-13T11:00:18.000Z"}}
            {"transaction": {"merchant": "Burger King", "amount": 190, "time": "2019-02-13T11:00:22.000Z"}}
            {"transaction": {"merchant": "Burger King", "amount": 15, "time": "2019-02-13T12:00:27.000Z"}}
         */
        List<Object> in = new ArrayList<>();
        in.add(new Account(true, 100));
        in.add(new Account(true, 60));
        in.add(new Transaction("McDonald's", 10, LocalDateTime.parse("2019-02-13T11:00:01.000Z",formatter)));
        in.add(new Transaction("Burger King", 20, LocalDateTime.parse("2019-02-13T11:00:02.000Z",formatter)));
        in.add(new Transaction("Burger King", 5, LocalDateTime.parse("2019-02-13T11:00:07.000Z",formatter)));
        in.add(new Transaction("Burger King", 5, LocalDateTime.parse("2019-02-13T11:00:08.000Z",formatter)));
        in.add(new Transaction("Burger King", 150, LocalDateTime.parse("2019-02-13T11:00:18.000Z",formatter)));
        in.add(new Transaction("Burger King", 190, LocalDateTime.parse("2019-02-13T11:00:22.000Z",formatter)));
        in.add(new Transaction("Burger King", 15, LocalDateTime.parse("2019-02-13T12:00:27.000Z",formatter)));

         /*
           # Output
            {"account":{"active-card":true,"available-limit":100},"violations":[]}
            {"account":{"active-card":true,"available-limit":100},"violations":["account-already-initialized"]}
            {"account":{"active-card":true,"available-limit":90},"violations":[]}
            {"account":{"active-card":true,"available-limit":70},"violations":[]}
            {"account":{"active-card":true,"available-limit":65},"violations":[]}
            {"account":{"active-card":true,"available-limit":65},"violations":["high-frequency-small-interval","doubled-transaction"]}
            {"account":{"active-card":true,"available-limit":65},"violations":["insufficient-limit","high-frequency-small-interval"]}
            {"account":{"active-card":true,"available-limit":65},"violations":["insufficient-limit","high-frequency-small-interval"]}
            {"account":{"active-card":true,"available-limit":50},"violations":[]}
         */
        List<AuthorizedTransaction> out = new ArrayList<>();
        List<String> violations_set0 = new ArrayList<>();
        List<String> violations_set1 = new ArrayList<>();
        List<String> violations_set2 = new ArrayList<>();
        violations_set0.add(RuleValidator.ACCOUNT_ALREADY_INITIALIZED.getValidation());
        violations_set1.add(RuleValidator.DOUBLE_TRANSACTION.getValidation());
        violations_set1.add(RuleValidator.HIGH_FREQUENCY_SMALL_INTERVAL.getValidation());
        violations_set2.add(RuleValidator.INSUFFICIENT_LIMIT.getValidation());
        violations_set2.add(RuleValidator.HIGH_FREQUENCY_SMALL_INTERVAL.getValidation());

        out.add(new AuthorizedTransaction(true, 100, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,100, violations_set0));
        out.add(new AuthorizedTransaction(true,90, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,70, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,65, new ArrayList<>()));
        out.add(new AuthorizedTransaction(true,65, violations_set1));
        out.add(new AuthorizedTransaction(true,65, violations_set2));
        out.add(new AuthorizedTransaction(true,65, violations_set2));
        out.add(new AuthorizedTransaction(true,50, new ArrayList<>()));

        assertEquals(
                authorizerService.authorize(in),
                out);
    }
}
