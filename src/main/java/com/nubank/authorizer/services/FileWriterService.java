package com.nubank.authorizer.services;

import com.nubank.authorizer.entities.AuthorizedTransaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Provides services to enable file writing.
 */
public class FileWriterService {

    private static final String FILE_NAME = "authorized-operations";

    /**
     * Generates an output file with the data it receives as a parameter under a static name.
     * @param data the data to be written to the file.
     */
    public void generateOutputFile(List<AuthorizedTransaction> data) {
        try {
            File file = new File(FILE_NAME);
            FileWriter writer = new FileWriter(file, false);
            for (AuthorizedTransaction item : data) {
                writer.write(item.toString() + "\n");
            }
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
