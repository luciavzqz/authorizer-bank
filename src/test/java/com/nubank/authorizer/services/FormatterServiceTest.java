package com.nubank.authorizer.services;

import com.nubank.authorizer.entities.Account;
import com.nubank.authorizer.entities.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FormatterServiceTest {

    FormatterService facilitiesService;

    @BeforeEach
    void setUp() {
        facilitiesService = new FormatterService();
    }

    @Test
    @DisplayName("Format data should work")
    void testFormatData() {
        List<String> dataInTest = new ArrayList<>();
        dataInTest.add("{\"account\": {\"active-card\": true, \"available-limit\": 100}}");
        dataInTest.add("{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\": \"2019-02-13T10:00:00.000Z\"}}");
        dataInTest.add("{\"transaction\": {\"merchant\": \"Habbib's\", \"amount\": 90, \"time\": \"2019-02-13T11:00:00.000Z\"}}");
        dataInTest.add("{\"transaction\": {\"merchant\": \"McDonald's\", \"amount\": 30, \"time\": \"2019-02-13T12:00:00.000Z\"}}");
        List<Object> dataOutTest = new ArrayList<>();
        dataOutTest.add(new Account(true, 100));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dataOutTest.add(new Transaction("Burger King",20, LocalDateTime.parse("2019-02-13T10:00:00.000Z",formatter)));
        dataOutTest.add(new Transaction("Habbib's",90, LocalDateTime.parse("2019-02-13T11:00:00.000Z",formatter)));
        dataOutTest.add(new Transaction("McDonald's",30, LocalDateTime.parse("2019-02-13T12:00:00.000Z",formatter)));
        assertEquals(
                dataOutTest,
                facilitiesService.formatData(dataInTest),
                "Format data should work");
    }
}
