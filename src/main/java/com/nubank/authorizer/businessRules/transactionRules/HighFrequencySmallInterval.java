package com.nubank.authorizer.businessRules.transactionRules;

import com.nubank.authorizer.businessRules.BusinessRule;
import com.nubank.authorizer.entities.AuthorizedTransaction;
import com.nubank.authorizer.entities.Transaction;
import com.nubank.authorizer.entities.ValidatedTransaction;
import com.nubank.authorizer.enums.RuleValidator;
import com.nubank.authorizer.enums.TransactionType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *  Determines that a transaction is not valid if in the same 2 minutes time window there have already been 3 successful
 *  transactions.
 *
 *  Example: given an account with an active card ( active-card: true ), the available limit of 100 ( available-limit: 100 ),
 *  and 3 transactions that occurred successfully in the last 2 minutes. The Authorizer should reject the new operation
 *  and return the high-frequency-small-interval violation.
 */
public class HighFrequencySmallInterval extends BusinessRule {

    private static final int MAX_FREQUENCY = 3;
    private static final int INTERVAL_MINUTES = 2;

    public HighFrequencySmallInterval(BusinessRule nextBusinessRule) {
        super(nextBusinessRule);
    }

    @Override
    protected List<ValidatedTransaction> validate(List<ValidatedTransaction> data) {
        Queue<LocalDateTime> queue = new LinkedList();
        for (ValidatedTransaction currentItem: data) {
            if (currentItem.getTransactionType().equals(TransactionType.TRANSACTION)) {
                // Determines the logic if it were a transaction TransactionType.
                Transaction transaction = (Transaction) currentItem.getTransaction();
                LocalDateTime currentTransaction = transaction.getTime();
                AuthorizedTransaction authorizedTransaction = currentItem.getAuthorizedTransaction();

                if(queue.size() + 1 > MAX_FREQUENCY) {
                    LocalDateTime lastValidTransaction = queue.peek();
                    // Less than 2 minutes is a violation
                    Long diff = ChronoUnit.MINUTES.between(lastValidTransaction, currentTransaction);
                    if(diff <= INTERVAL_MINUTES) {
                        authorizedTransaction.getViolations().add(RuleValidator.HIGH_FREQUENCY_SMALL_INTERVAL.getValidation());
                    } else {
                        // If it is more than 2 minutes I will update the data due to the transaction is valid
                        queue.remove();
                        queue.add(currentTransaction);
                    }
                } else if(authorizedTransaction.getViolations().isEmpty()){
                    // If there are not 3 valid transactions, I add the current one if it's a valid transaction.
                    queue.add(currentTransaction);
                }
            }
        }
        return data;
    }
}
