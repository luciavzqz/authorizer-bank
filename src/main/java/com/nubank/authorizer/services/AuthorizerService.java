package com.nubank.authorizer.services;

import com.nubank.authorizer.businessRules.BusinessRulesManager;
import com.nubank.authorizer.entities.AuthorizedTransaction;

import java.util.List;

public class AuthorizerService {
    private BusinessRulesManager businessRulesManager = new BusinessRulesManager();

    public List<AuthorizedTransaction> authorize(List<Object> data) {
        List<AuthorizedTransaction> authorizedData = businessRulesManager.runValidators(data);
        return authorizedData;
    }
}