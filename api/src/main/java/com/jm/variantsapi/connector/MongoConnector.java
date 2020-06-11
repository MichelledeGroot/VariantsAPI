package com.jm.variantsapi.connector;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@EnableAutoConfiguration
@Configuration
public class MongoConnector {

    private static MongoCollection<Document> variantsColl;

    final static Logger logger = LoggerFactory.getLogger(MongoConnector.class);

    /**
     * Uses chromosome and position to retrieve variants from the possible pathogenic MongoDB
     *
     * @param chromosome Chromosome of the variant
     * @param position Position of the variant
     * @return ArrayList with possible pathogenic variants from the MongoDB
     */
    public static ArrayList<String> getVariantFromDatabase(String chromosome, String position){
        BasicDBObject query = new BasicDBObject("Position", position);
        query.put("Chromosome", chromosome);;
        ArrayList<String> results = new ArrayList();
        try (MongoCursor<Document> cursor = variantsColl.find(query).projection(Projections.fields(
                Projections.include("Chromosome", "Position", " variant_id", "Reference", "Alternate"),
                Projections.excludeId()))
                .iterator()) {
            while(cursor.hasNext()) {
                results.add(cursor.next().toJson());
            }
        } catch ( NullPointerException ex ){
            logger.warn("Database is empty or failed to load");
        }
        return results;
    }

    /**
     * MongoDB connector
     * Makes a connection to the database on startup of the application
     */
    @Bean
    public void MongoClientConnector() {
        logger.error("help");
        try {
            MongoClient mongoClient = new MongoClient("database", 27017);
            MongoDatabase database = mongoClient.getDatabase("variantsdatabase");
            for (String name : database.listCollectionNames()) {
                variantsColl = database.getCollection(name);
                logger.info("Collection name: \u001B[34m" + name + "\u001B[0m");
            }
        } catch ( Exception e ) {
            logger.error("Caught Exception in login(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
