package com.nubank.authorizer.entities;

import com.nubank.authorizer.interfaces.GenericTransaction;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
@Setter
public class Account implements GenericTransaction {
    private Boolean activeCard;
    private Integer availableLimit;
}
