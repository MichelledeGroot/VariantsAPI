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
        String user = "JMT"; // the user name
        String databasename = "variantsaapies"; // the name of the database in which the user is defined
        String password = "variantsaapies"; //password
        char[] passwordArray = password.toCharArray();
        //logger.info("Connecting to database");

        MongoCredential credential = MongoCredential.createCredential(user, databasename, passwordArray);
        MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017),
                                                    Arrays.asList(credential));
        //TODO: Method is deprecated, find other way for credentials
        MongoDatabase database = mongoClient.getDatabase(databasename);
        for (String name : database.listCollectionNames()) {
            System.out.println(name);
        }
    }

}
