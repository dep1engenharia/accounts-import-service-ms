package com.ogamex.accounts_import_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogamex.accounts_import_service.dto.AccountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SeedConsumer.class);
    private static final String FILE_PATH = "created_accounts.json";

    private final SeedService seedService;
    private final ObjectMapper objectMapper;

    @Autowired
    public SeedConsumer(SeedService seedService) {
        this.seedService = seedService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(
            topics = "accounts",
            groupId = "accounts-group"
    )
    public void listen(AccountDTO account) {
        logger.info("Consumed account from broker: {}", account.getEmail());
        seedService.seedByEmail(account.getEmail());
        appendAccountToFile(account);
    }

    private synchronized void appendAccountToFile(AccountDTO account) {
        File file = Paths.get(FILE_PATH).toFile();
        List<AccountDTO> accountsList;

        // Step 1: Read existing list (if file exists), else create new list
        if (file.exists()) {
            try {
                accountsList = objectMapper.readValue(file, new TypeReference<>() {});
            } catch (IOException e) {
                logger.error("Failed to read existing accounts file, starting new list", e);
                accountsList = new ArrayList<>();
            }
        } else {
            accountsList = new ArrayList<>();
        }

        // Step 2: Append new account
        accountsList.add(account);

        // Step 3: Write updated list back to file
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, accountsList);
            logger.info("Appended account to file: {} (total records: {})", account.getEmail(), accountsList.size());
        } catch (IOException e) {
            logger.error("Failed to write accounts to file", e);
        }
    }
}
