package com.nubank.authorizer.interfaces;

import com.nubank.authorizer.entities.AuthorizedTransaction;

import java.util.List;

/**
 * Defines the generic interface for a rule manager.
 */
public interface RulesManager {
    List<AuthorizedTransaction> runValidators(List<Object> data);
}
