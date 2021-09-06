package com.nubank.authorizer.businessRules.transactionRules;

import com.nubank.authorizer.businessRules.Rule;
import com.nubank.authorizer.entities.Account;
import com.nubank.authorizer.entities.AuthorizedTransaction;
import com.nubank.authorizer.entities.Transaction;
import com.nubank.authorizer.entities.ValidatedTransaction;
import com.nubank.authorizer.enums.RuleValidator;
import com.nubank.authorizer.enums.TransactionType;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DoubleTransaction extends Rule {

    private static final int INTERVAL_MINUTES = 2;

    public DoubleTransaction(Rule nextRule) {
        super(nextRule);
    }

    @Override
    protected List<ValidatedTransaction> validate(List<ValidatedTransaction> data) {
        Map<String, Map<Integer, LocalDateTime>> transactionsSeen = new HashMap<>();

        for (ValidatedTransaction currentItem: data) {
            if(currentItem.getTransactionType().equals(TransactionType.TRANSACTION)){
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
