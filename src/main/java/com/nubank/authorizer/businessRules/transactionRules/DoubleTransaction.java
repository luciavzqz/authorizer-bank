package com.nubank.authorizer.businessRules.transactionRules;

import com.nubank.authorizer.businessRules.BusinessRule;
import com.nubank.authorizer.entities.AuthorizedTransaction;
import com.nubank.authorizer.entities.Transaction;
import com.nubank.authorizer.entities.ValidatedTransaction;
import com.nubank.authorizer.enums.RuleValidator;
import com.nubank.authorizer.enums.TransactionType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Determines that a transaction is not valid if in the same 2 minutes time window there has already been another
 *  transaction to the same merchant and with the same amount.
 *
 *  Example: given an account with an active card ( active-card: true ), the available limit of 100 ( available-limit:
 *  100 ) and some transactions that occurred successfully in the last 2 minutes. The authorizer should reject the new
 *  transaction if it shares the same amount and merchant as any of previously accepted transactions and return the
 *  doubled-transaction violation.
 */
public class DoubleTransaction extends BusinessRule {

    private static final int INTERVAL_MINUTES = 2;

    public DoubleTransaction(BusinessRule nextBusinessRule) {
        super(nextBusinessRule);
    }

    @Override
    protected List<ValidatedTransaction> validate(List<ValidatedTransaction> data) {
        Map<String, Map<Integer, LocalDateTime>> transactionsSeen = new HashMap<>();

        for (ValidatedTransaction currentItem: data) {
            if(currentItem.getTransactionType().equals(TransactionType.TRANSACTION)){
                // Determines the logic if it were a transaction TransactionType.
                Transaction transaction = (Transaction) currentItem.getTransaction();
                AuthorizedTransaction authorizedTransaction = currentItem.getAuthorizedTransaction();

                // Is a valid Transaction
                if(authorizedTransaction.getViolations().isEmpty()) {
                    String merchant = transaction.getMerchant();
                    LocalDateTime date = transaction.getTime();
                    Integer amount = transaction.getAmount();

                    if(transactionsSeen.containsKey(merchant) ) {
                        if(transactionsSeen.get(merchant).containsKey(amount)) {
                            LocalDateTime lastValidTransaction = transactionsSeen.get(merchant).get(amount);
                            // Less than 2 minutes is a violation
                            Long diff = ChronoUnit.MINUTES.between(lastValidTransaction, date);
                            if(diff <= INTERVAL_MINUTES) {
                                authorizedTransaction.getViolations().add(RuleValidator.DOUBLE_TRANSACTION.getValidation());
                            } else { // If it is more than 2 minutes I will update the data
                                transactionsSeen.get(merchant).put(amount, date);
                            }
                        } else {
                            transactionsSeen.get(merchant).put(amount, date);
                        }
                    } else {
                        Map<Integer, LocalDateTime> item = new HashMap<>();
                        item.put(amount, date);
                        transactionsSeen.put(merchant,item);
                    }
                }
            }
        }
        return data;
    }
}
