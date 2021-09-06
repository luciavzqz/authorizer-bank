package com.nubank.authorizer.businessRules.transactionRules;

import com.nubank.authorizer.businessRules.BusinessRule;
import com.nubank.authorizer.entities.Account;
import com.nubank.authorizer.entities.AuthorizedTransaction;
import com.nubank.authorizer.entities.Transaction;
import com.nubank.authorizer.entities.ValidatedTransaction;
import com.nubank.authorizer.enums.RuleValidator;
import com.nubank.authorizer.enums.TransactionType;

import java.util.List;

/**
 *  Determines that a transaction is not valid if the amount of the transaction is greater than the amount available in
 *  the account.
 *
 *  Example: given an account with an active card ( active-card: true ), the available limit of 1000 ( available-limit:
 *  1000 ), any transaction above the limit of 1000 should be rejected and return the insufficient-limit violation.
 */
public class InsufficientLimit extends BusinessRule {

    public InsufficientLimit(BusinessRule nextBusinessRule) {
        super(nextBusinessRule);
    }

    @Override
    protected List<ValidatedTransaction> validate(List<ValidatedTransaction> data) {
        Boolean accountInitialized = false;
        Integer currentAvailableLimit = null;
        Boolean activeCard = null;

        for (ValidatedTransaction currentItem: data) {
            if(currentItem.getTransactionType().equals(TransactionType.ACCOUNT)){
                // Determines the logic if it were an account TransactionType.
                if(!accountInitialized) {
                    accountInitialized = true;
                    Account account = (Account) currentItem.getTransaction();
                    currentAvailableLimit = account.getAvailableLimit();
                    activeCard = account.getActiveCard();
                }
            } else {
                // Determines the logic if it were a transaction TransactionType.
                if(accountInitialized && activeCard) {
                    Transaction transaction = (Transaction) currentItem.getTransaction();
                    Integer newAvailableLimit = currentAvailableLimit - transaction.getAmount();
                    AuthorizedTransaction authorizedTransaction = currentItem.getAuthorizedTransaction();
                    if (newAvailableLimit >= 0) {
                        currentAvailableLimit = newAvailableLimit;
                    } else {
                        authorizedTransaction.getViolations().add(RuleValidator.INSUFFICIENT_LIMIT.getValidation());
                    }
                }
            }
        }
        return data;
    }
}
