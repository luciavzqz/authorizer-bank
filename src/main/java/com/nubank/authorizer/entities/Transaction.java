package com.nubank.authorizer.entities;

import com.nubank.authorizer.interfaces.GenericTransaction;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
@Setter
public class Transaction  implements GenericTransaction {
    private String merchant;
    private Integer amount;
    private LocalDateTime time;
}
