package com.jm.variantsapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import com.jm.variantsapi.storage.StorageService;

/**
 * Main class for running the spring application
 *
 * @author Michelle de Groot, Joshua Koopmans
 * @version 1.0
 * @date 7/6/2020
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class VariantsapiApplication {

    final Logger logger = LoggerFactory.getLogger(VariantsapiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(VariantsapiApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }
}
