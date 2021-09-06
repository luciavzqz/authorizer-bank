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
 *  Determines whether a transaction is valid based on whether the card with which the account was initiated is active.
 *  If the account has a card that is not active, the transaction will not be valid.
 *
 *  Example: given an account with an inactive card ( active-card: false ), any transaction submit to the Authorizer
 *  should be rejected and return the card-not-active violation.
 */
public class CardNotActive extends BusinessRule {

    public CardNotActive(BusinessRule nextBusinessRule) {
        super(nextBusinessRule);
    }

    @Override
    protected List<ValidatedTransaction> validate(List<ValidatedTransaction> data) {
        Optional<Account> accountInitializedOpt = Optional.empty();

        for (ValidatedTransaction currentItem : data) {
            if (currentItem.getTransactionType().equals(TransactionType.ACCOUNT)) {
                // Determines the logic if it were an account TransactionType.
                if (!accountInitializedOpt.isPresent()) {
                    accountInitializedOpt = Optional.of((Account) currentItem.getTransaction());
                }
            } else {
                // Determines the logic if it were a transaction TransactionType.
                if (accountInitializedOpt.isPresent() && !accountInitializedOpt.get().getActiveCard()) {
                    AuthorizedTransaction authorizedTransaction = currentItem.getAuthorizedTransaction();
                    authorizedTransaction.getViolations().add(RuleValidator.CARD_NOT_ACTIVE.getValidation());
                }
            }
        }
        return data;
    }
}
