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
 * This application can create a JSON file and an annotated tsv file based on a tsv file with
 * chromosomes and positions. The variants in tje JSON file can be used for further research
 *
 * @author Michelle de Groot, Joshua Koopmans
 * @version 1.0
 * @date 7/6/2020
 */
@SpringBootApplication()
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
