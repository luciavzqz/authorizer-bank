package com.nubank.authorizer.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
@Setter
public class AuthorizedTransaction {
    private Boolean activeCard;
    private Integer availableLimit;
    private List<String> violations;

    @Override
    public String toString() {
        String s;
        if(Optional.ofNullable(activeCard).isPresent()){
            // Example: {"account": {"active-card": true, "available-limit": 100}, "violations": []}
            s = "{\"account\": " +
                    "{\"active-card\": " + activeCard + ", \"available-limit\": " + availableLimit + "}, " +
                    "\"violations\": " + violations.toString() + "}";
        } else {
            // Example: {"account": {}, "violations": ["account-not-initialized"]}
            s = "{\"account\": {}, \"violations\": " + violations.toString() + "}";;
        }
        return s;
    }
}
