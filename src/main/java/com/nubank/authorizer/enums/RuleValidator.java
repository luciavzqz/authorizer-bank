package com.nubank.authorizer.enums;

import lombok.Getter;

@Getter
/**
 *  Contains the names of the current validations of the business rules system.
 */
public enum RuleValidator {
    DOUBLE_TRANSACTION("double-transaction"),
    HIGH_FREQUENCY_SMALL_INTERVAL("high-frequency-small-interval"),
    ACCOUNT_ALREADY_INITIALIZED("account-already-initialized"),
    ACCOUNT_NOT_INITIALIZED("account-not-initialized"),
    CARD_NOT_ACTIVE("card-not-active"),
    INSUFFICIENT_LIMIT("insufficient-limit");

    private String validation;

    RuleValidator(String validation) {
        this.validation = validation;
    }
}
