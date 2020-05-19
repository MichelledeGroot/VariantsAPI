package com.jm.variantsapi;

import com.jm.variantsapi.connector.MongoConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VariantController {

    final Logger logger = LoggerFactory.getLogger(VariantController.class);

    @GetMapping("/")
    public String index(@RequestParam(value = "chromosome", defaultValue = "") String chromosome,
                        @RequestParam(value = "position", defaultValue = "") String position) {
        return String.valueOf(MongoConnector.getVariantFromDatabase(chromosome, position));
    }

}