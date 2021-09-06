package com.nubank.authorizer.businessRules;

import com.nubank.authorizer.entities.Account;
import com.nubank.authorizer.entities.AuthorizedTransaction;
import com.nubank.authorizer.entities.ValidatedTransaction;
import com.nubank.authorizer.interfaces.GenericTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BusinessRulesTest {
    DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    @Test
    @DisplayName("FormatData works correctly")
    void formatDataTest(){
        BusinessRulesManager b = new BusinessRulesManager();

        GenericTransaction a1 = new Account(false, 750);
        List<Object> in = new ArrayList<>();
        in.add(a1);

        List<ValidatedTransaction> out = new ArrayList<>();
        out.add(new ValidatedTransaction(a1, new AuthorizedTransaction(null,null, new ArrayList<>())));

        assertEquals(
                b.formatData(in),
                out
        );
    }
}
