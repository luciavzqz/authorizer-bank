package com.nubank.authorizer.businessRules.accountRules;

import com.nubank.authorizer.businessRules.Rule;
import com.nubank.authorizer.entities.Account;
import com.nubank.authorizer.entities.AuthorizedTransaction;
import com.nubank.authorizer.entities.ValidatedTransaction;
import com.nubank.authorizer.enums.RuleValidator;
import com.nubank.authorizer.enums.TransactionType;
import java.util.List;
import java.util.Optional;

public class AccountAlreadyInitialized extends Rule {

    public AccountAlreadyInitialized(Rule nextRule) {
        super(nextRule);
    }

    @Override
    protected List<ValidatedTransaction> validate(List<ValidatedTransaction> data) {
        Optional<Account> accountInitializedOpt = Optional.empty();
        for (ValidatedTransaction dataItem: data) {
            if(dataItem.getTransactionType().equals(TransactionType.ACCOUNT)){
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
            } else { // transaction
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
