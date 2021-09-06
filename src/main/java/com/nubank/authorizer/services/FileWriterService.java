package com.nubank.authorizer.services;

import com.nubank.authorizer.entities.AuthorizedTransaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileWriterService {
    private static final String FILE_NAME = "authorized-operations";
    public void generateOutputFile(List<AuthorizedTransaction> data) {
        try {
            File file = new File(FILE_NAME);
            FileWriter writer = new FileWriter(file, true);
            for (AuthorizedTransaction item : data) {
                writer.write(item.toString() + "\n");
            }
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
//        data.stream().forEach( System.out::println );
    }
}
