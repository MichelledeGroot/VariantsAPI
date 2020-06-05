package com.jm.variantsapi.connector;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;


@Configuration
public class MongoConnector {

    static MongoCollection<Document> variantsColl;
    final Logger logger = LoggerFactory.getLogger(MongoConnector.class);

    public static ArrayList<String> getVariantFromDatabase(String chromosome, String position){
        BasicDBObject query = new BasicDBObject("Position", position);
        query.put("Chromosome", chromosome);
        ArrayList<String> results = new ArrayList();
        try (MongoCursor<Document> cursor = variantsColl.find(query).projection(Projections
                .include("Chromosome", "Position", " variant_id", "Reference", "Alternate"))
                .iterator()) {
            while(cursor.hasNext()) {
                results.add(cursor.next().toJson());
            }
        }
        return results;
    }

    @Bean
    public void MongoClientConnector() {
        try {
            MongoClient mongoClient = new MongoClient("database", 27017);
            MongoDatabase database = mongoClient.getDatabase("variantsdatabase");
            for (String name : database.listCollectionNames()) {
                variantsColl = database.getCollection(name);
                FindIterable<Document> variants = variantsColl.find();
                logger.info("Collection name: \u001B[34m" + name + "\u001B[0m");
            }
        } catch ( Exception e ) {
            logger.error("Caught Exception in login(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
