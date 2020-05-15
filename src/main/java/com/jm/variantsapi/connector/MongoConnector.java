package com.jm.variantsapi.connector;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.Arrays;

@Configuration
public class MongoConnector {

    final Logger logger = LoggerFactory.getLogger(MongoConnector.class);

    @Bean
    public void MongoClientConnector() {
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        MongoDatabase database = mongoClient.getDatabase("variantsdatabase");
        for (String name : database.listCollectionNames()) {
            System.out.println("Collection name: \u001B[34m"+name+"\u001B[0m");
        }
    }

}
