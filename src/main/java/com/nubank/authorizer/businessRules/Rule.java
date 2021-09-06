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
public abstract class Rule {
    public Rule nextRule;

    public List<ValidatedTransaction> runValidator(List<ValidatedTransaction> data){
        List<ValidatedTransaction> validatedTransactions = validate(data);

        Optional<Rule> nextRuleOptional = Optional.ofNullable(getNextRule());
        if(nextRuleOptional.isPresent()) {
            return nextRuleOptional.get().runValidator(validatedTransactions);
        }
        return validatedTransactions;
    }

    protected abstract List<ValidatedTransaction> validate(List<ValidatedTransaction> data);

}
