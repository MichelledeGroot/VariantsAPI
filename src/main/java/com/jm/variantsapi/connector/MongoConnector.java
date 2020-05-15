package com.jm.variantsapi.connector;

import com.jm.variantsapi.VariantsapiApplication;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.MongoCredential;
import com.mongodb.MongoClientOptions;
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
        //logger.info("Connecting to database");
        MongoClient mongoClient = new MongoClient();
    }

}
