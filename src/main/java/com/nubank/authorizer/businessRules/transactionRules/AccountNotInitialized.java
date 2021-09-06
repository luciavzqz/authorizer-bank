package com.nubank.authorizer.businessRules.transactionRules;

import com.nubank.authorizer.businessRules.BusinessRule;
import com.nubank.authorizer.entities.Account;
import com.nubank.authorizer.entities.AuthorizedTransaction;
import com.nubank.authorizer.entities.ValidatedTransaction;
import com.nubank.authorizer.enums.RuleValidator;
import com.nubank.authorizer.enums.TransactionType;
import java.util.List;
import java.util.Optional;

/**
 *  Determines whether a transaction is invalid based on whether an account has been initialized correctly.
 *
 *  Example: when a transaction operation is processed but there is not a previously created account, the Authorizer
 *  should return the account-not-initialized violation.
 */
public class AccountNotInitialized extends BusinessRule {

    public AccountNotInitialized(BusinessRule nextBusinessRule) {
        super(nextBusinessRule);
    }

    @Override
    protected List<ValidatedTransaction> validate(List<ValidatedTransaction> data) {
        Optional<Account> accountInitializedOpt = Optional.empty();

        for (ValidatedTransaction currentItem: data) {
            if(currentItem.getTransactionType().equals(TransactionType.ACCOUNT)){
                // Determines the logic if it were an account TransactionType.
                if(!accountInitializedOpt.isPresent()) {
                    accountInitializedOpt = Optional.of((Account) currentItem.getTransaction());
                }
            } else {
                // Determines the logic if it were a transaction TransactionType.
                if(!accountInitializedOpt.isPresent()) {
                    AuthorizedTransaction authorizedTransaction = currentItem.getAuthorizedTransaction();
                    authorizedTransaction.getViolations().add(RuleValidator.ACCOUNT_NOT_INITIALIZED.getValidation());
                }
            }
        }
        return data;
    }
}
