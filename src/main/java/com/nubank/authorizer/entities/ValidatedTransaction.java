package com.nubank.authorizer.entities;

import com.nubank.authorizer.enums.TransactionType;
import com.nubank.authorizer.interfaces.GenericTransaction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@EqualsAndHashCode
@Getter
@Setter
public class ValidatedTransaction {
    private GenericTransaction transaction;
    private AuthorizedTransaction authorizedTransaction;

    public ValidatedTransaction(GenericTransaction transaction, AuthorizedTransaction authorizedTransaction) {
        this.transaction = transaction;
        this.authorizedTransaction = authorizedTransaction;
    }

    public TransactionType getTransactionType() {
        if(this.transaction.getClass().equals(Account.class))
            return TransactionType.ACCOUNT;
        return TransactionType.TRANSACTION;
    }
}