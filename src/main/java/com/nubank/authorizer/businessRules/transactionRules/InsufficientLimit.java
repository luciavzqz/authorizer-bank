package com.nubank.authorizer.businessRules.transactionRules;

import com.nubank.authorizer.businessRules.Rule;
import com.nubank.authorizer.entities.Account;
import com.nubank.authorizer.entities.AuthorizedTransaction;
import com.nubank.authorizer.entities.Transaction;
import com.nubank.authorizer.entities.ValidatedTransaction;
import com.nubank.authorizer.enums.RuleValidator;
import com.nubank.authorizer.enums.TransactionType;

import java.util.List;

public class InsufficientLimit extends Rule {

    public InsufficientLimit(Rule nextRule) {
        super(nextRule);
    }

    @Override
    protected List<ValidatedTransaction> validate(List<ValidatedTransaction> data) {
        Boolean accountInitialized = false;
        Integer currentAvailableLimit = null;
        Boolean activeCard = null;

        for (ValidatedTransaction currentItem: data) {
            if(currentItem.getTransactionType().equals(TransactionType.ACCOUNT)){
                if(!accountInitialized) {
                    accountInitialized = true;
                    Account account = (Account) currentItem.getTransaction();
                    currentAvailableLimit = account.getAvailableLimit();
                    activeCard = account.getActiveCard();
                }
            } else { // transaction
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
