package com.nubank.authorizer.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nubank.authorizer.entities.Account;
import com.nubank.authorizer.entities.Transaction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FormatterService {

    private static final String ACCOUNT = "account";
    private static final String ACTIVE_CARD = "active-card";
    private static final String  AVAILABLE_LIMIT= "available-limit";
    private static final String TRANSACTION = "transaction";
    private static final String MERCHANT = "merchant";
    private static final String AMOUNT = "amount";
    private static final String TIME = "time";

    public List<Object> getDataFromFile(final File file) {
        List<String> formattedFile = formatFile(file);
        return formatData(formattedFile);
    }

    public List<String> formatFile(final File file) {
        List<String> jsonStringList = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get(file.getAbsolutePath()))){
            jsonStringList = lines.collect(Collectors.toList());
        } catch(IOException e){
            e.printStackTrace();
        }
        return jsonStringList;
    }

    public List<Object> formatData(final List<String> data) {
        List<Object> formattedData = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (String line : data) {

        // TODO : DELETE
//            Map<String, String> map = null;
//            try {
//                map = mapper.readValue(line, Map.class);
//
//                ObjectMapper objectMapper = new ObjectMapper();
//                String value;
//                if(map.containsKey(ACCOUNT)) {
//                    value = map.get(ACCOUNT);
//                    Account account = objectMapper.readValue(value, Account.class);
//                    formattedData.add(account);
//                }
//                else { //contains TRANSACTION
//                    value = map.get(TRANSACTION);
//                    Transaction transaction = objectMapper.readValue(value, Transaction.class);
//                    formattedData.add(transaction);
//                }
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }

            Map<String, Map<String, Object>> map = null;
            try {
                map = mapper.readValue(line, Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(map.containsKey(ACCOUNT)){
                Map<String, Object> value = map.get(ACCOUNT);
                formattedData.add(new Account(
                        (Boolean) value.get(ACTIVE_CARD),
                        (Integer) value.get(AVAILABLE_LIMIT)));
            }
            else { //contains TRANSACTION
                Map<String, Object> value = map.get(TRANSACTION);
                String time = (String) value.get(TIME);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
                formattedData.add(new Transaction(
                        (String) value.get(MERCHANT),
                        (Integer) value.get(AMOUNT),
                        dateTime));
            }
        }
        return formattedData;
    }
}
