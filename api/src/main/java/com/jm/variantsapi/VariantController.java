package com.jm.variantsapi;

import com.jm.variantsapi.connector.MongoConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.ArrayList;

@RestController
public class VariantController {

    final Logger logger = LoggerFactory.getLogger(VariantController.class);

    @GetMapping(value = "/")
    public String index(@RequestParam(value = "chromosome", defaultValue = "") String chromosome,
                        @RequestParam(value = "position", defaultValue = "") String position) {
        ArrayList<String> variantsList = MongoConnector.getVariantFromDatabase(chromosome, position);
        if (variantsList.size() == 0) {
            return "No possible pathogenic variants for this position";
        } else {
            return String.join("\n", variantsList);
        }
    }
}