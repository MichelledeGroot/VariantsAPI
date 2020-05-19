package com.jm.variantsapi.connector;

import com.mongodb.MongoClient;
import com.mongodb.MongoSocketException;
import com.mongodb.MongoSocketOpenException;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;


@Configuration
public class MongoConnector {

    final Logger logger = LoggerFactory.getLogger(MongoConnector.class);

    @Bean
    public void MongoClientConnector() {
        try {
            MongoClient mongoClient = new MongoClient("database", 27017);
            MongoDatabase database = mongoClient.getDatabase("variantsdatabase");
            for (String name : database.listCollectionNames()) {
                logger.info("Colection name: \u001B[34m" + name + "\u001B[0m");
                }
        } catch (Exception e) {
            logger.error("Caught Exception in login(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

}
