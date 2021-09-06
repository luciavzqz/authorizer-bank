package com.nubank.authorizer.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    ACCOUNT("Account","account"),
    TRANSACTION("Transaction","transaction");

    private String className;
    private String name;
    TransactionType(final String className, final String name) {
        this.className = className;
        this.name = name;
    }

    public static TransactionType valueOfClassName(final String label) {
        for (TransactionType e : values()) {
            if (e.className.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
