package com.nubank.authorizer.services;

import com.nubank.authorizer.businessRules.BusinessRulesManager;
import com.nubank.authorizer.entities.AuthorizedTransaction;

import java.util.List;

/**
 * Provides services to enable transaction authorization.
 */
public class AuthorizerService {

    private BusinessRulesManager businessRulesManager = new BusinessRulesManager();

    /**
     * It authorizes the transactions it receives as a parameter.
     * @param data is the list of transactions to be authorized.
     * @return the list of authorized transactions.
     */
    public List<AuthorizedTransaction> authorize(List<Object> data) {
        List<AuthorizedTransaction> authorizedData = businessRulesManager.runValidators(data);
        return authorizedData;
    }
}