package com.nubank.authorizer.businessRules;

import com.nubank.authorizer.entities.ValidatedTransaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
@Setter
/**
 *  Represents the abstract class of a business rule. It is used for the application of the chain of responsibility pattern.
 */
public abstract class BusinessRule {
    public BusinessRule nextBusinessRule;

    /**
     * Contains the logic to run the validator and know if it should pass task to another business rule.
     * @param data the list of transactions to be validated.
     * @return list of validated transactions.
     */
    public List<ValidatedTransaction> runValidator(List<ValidatedTransaction> data){
        List<ValidatedTransaction> validatedTransactions = validate(data);

        Optional<BusinessRule> nextRuleOptional = Optional.ofNullable(getNextBusinessRule());
        if(nextRuleOptional.isPresent()) {
            return nextRuleOptional.get().runValidator(validatedTransactions);
        }
        return validatedTransactions;
    }

    /**
     * Contains the validator's own logic.
     * @param data the list of transactions to be validated.
     * @return the list of validated transactions.
     */
    protected abstract List<ValidatedTransaction> validate(List<ValidatedTransaction> data);

}
