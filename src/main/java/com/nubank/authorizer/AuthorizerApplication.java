package com.nubank.authorizer;

import com.nubank.authorizer.entities.AuthorizedTransaction;
import com.nubank.authorizer.services.AuthorizerService;
import com.nubank.authorizer.services.FileWriterService;
import com.nubank.authorizer.services.FormatterService;

import java.io.File;
import java.util.List;

public class AuthorizerApplication {
    static private FormatterService formatterService= new FormatterService();
    static private AuthorizerService authorizerService = new AuthorizerService();
    static private FileWriterService fileWriterService = new FileWriterService();

    public static void main(String[] args) {
        if (args.length == 1) {
            File file = new File(args[0]);
            if (file.isFile()){
                List<Object> data = formatterService.getDataFromFile(file);
                List<AuthorizedTransaction> transactionsAuthorize = authorizerService.authorize(data);
                fileWriterService.generateOutputFile(transactionsAuthorize);
            }else{
                System.out.println("Path is not a file");
            }
        } else if (args.length == 0){
            System.out.println("You must add an argument 'operations-file-path'. This must be the path to the file with the operations.");
        } else {
            System.out.println("There must be only one argument, 'operations-file-path'. This must be the path to the file with the operations.");
        }
    }
}