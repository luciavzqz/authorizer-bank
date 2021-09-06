package com.nubank.authorizer.businessRules.accountRules;

import com.nubank.authorizer.businessRules.BusinessRule;
import com.nubank.authorizer.entities.Account;
import com.nubank.authorizer.entities.AuthorizedTransaction;
import com.nubank.authorizer.entities.ValidatedTransaction;
import com.nubank.authorizer.enums.RuleValidator;
import com.nubank.authorizer.enums.TransactionType;
import java.util.List;
import java.util.Optional;

/**
 *  Determines whether an account has already been initialized in the transaction set and invalidates subsequent
 *  transactions.
 *
 *  Example: given there is an account with an active card ( active-card: true ) and the available limit of 175
 *  ( available-limit: 175 ), tries to create another account but return the account-already-initialized violation.
 */
public class AccountAlreadyInitialized extends BusinessRule {

    public AccountAlreadyInitialized(BusinessRule nextBusinessRule) {
        super(nextBusinessRule);
    }

    @Override
    protected List<ValidatedTransaction> validate(List<ValidatedTransaction> data) {
        Optional<Account> accountInitializedOpt = Optional.empty();
        for (ValidatedTransaction dataItem: data) {
            if(dataItem.getTransactionType().equals(TransactionType.ACCOUNT)){
                // Determines the logic if it were an account TransactionType.
                if(accountInitializedOpt.isPresent()) {
                    AuthorizedTransaction authorizedTransaction = dataItem.getAuthorizedTransaction();
                    Account accountInitialized = accountInitializedOpt.get();

                    authorizedTransaction.setActiveCard(accountInitialized.getActiveCard());
                    authorizedTransaction.setAvailableLimit(accountInitialized.getAvailableLimit());

                    authorizedTransaction.getViolations().add(RuleValidator.ACCOUNT_ALREADY_INITIALIZED.getValidation());
                } else {
                    accountInitializedOpt = Optional.of((Account) dataItem.getTransaction());

                    AuthorizedTransaction authorizedTransaction = dataItem.getAuthorizedTransaction();
                    authorizedTransaction.setActiveCard(accountInitializedOpt.get().getActiveCard());
                    authorizedTransaction.setAvailableLimit(accountInitializedOpt.get().getAvailableLimit());
                }
            } else {
                // Determines the logic if it were a transaction TransactionType.
                if(accountInitializedOpt.isPresent()) {
                    AuthorizedTransaction authorizedTransaction = dataItem.getAuthorizedTransaction();
                    Account accountInitialized = accountInitializedOpt.get();

                    authorizedTransaction.setActiveCard(accountInitialized.getActiveCard());
                    authorizedTransaction.setAvailableLimit(accountInitialized.getAvailableLimit());
                }
            }
        }
        return data;
    }
}
