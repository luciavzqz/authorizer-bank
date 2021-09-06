package com.nubank.authorizer.interfaces;

import com.nubank.authorizer.entities.AuthorizedTransaction;

import java.util.List;

public interface RulesManager {
    List<AuthorizedTransaction> runValidators(List<Object> data);
}
