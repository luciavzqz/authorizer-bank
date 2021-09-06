package com.nubank.authorizer.entities;

import com.nubank.authorizer.enums.TransactionType;
import com.nubank.authorizer.interfaces.GenericTransaction;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@EqualsAndHashCode
@AllArgsConstructor
@Getter
@Setter
/**
 * Represents a generic transaction in the authorization process.
 * Contains the transaction data and its current validation status.
 */
public class ValidatedTransaction {
    private GenericTransaction transaction;
    private AuthorizedTransaction authorizedTransaction;

    /**
     * Gets the transaction type of the generic transaction instance.
     * @return the transaction of the generic transaction instance.
     */
    public TransactionType getTransactionType() {
        if(this.transaction.getClass().equals(Account.class))
            return TransactionType.ACCOUNT;
        return TransactionType.TRANSACTION;
    }
}