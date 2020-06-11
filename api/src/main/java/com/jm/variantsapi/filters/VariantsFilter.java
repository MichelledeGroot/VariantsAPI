package com.jm.variantsapi.filters;

import com.jm.variantsapi.connector.MongoConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is responsible for reading the uploaded file and creating a annotated file and a JSON file
 * The annotation is either benign when there are no hits in the possible pathogenic varianst database
 * or pathogenic when the variant can be found in the possible pathogenic database
 * The possible pathogenic variants can be found in the JSON file
 *
 */
public class VariantsFilter {

    static final Logger logger = LoggerFactory.getLogger(VariantsFilter.class);
    static final String JSON = "json";
    static final String TSV = "tsv";

    /**
     * Uses the uploaded file to create a new annotated file
     * @param variantsFile the file that was uploaded by the user
     * @return MockMultipartFile to be stored
     */
    public static MockMultipartFile[] createAnnotatedFile(MultipartFile variantsFile){
        logger.info("Reading file: "+variantsFile.getOriginalFilename());
        HashMap<String, String> variantsHashMap = readAndAnnotateMultiPartFile(variantsFile);

        String baseName = FilenameUtils.removeExtension(variantsFile.getOriginalFilename());
        String annotatedName =  baseName + "_annotated";
        String annotatedFileName = annotatedName+".tsv";
        String jsonFileName = baseName + ".json";

        logger.info("Wrote annotations to: " + annotatedFileName);

        MockMultipartFile tsvFile = new MockMultipartFile(annotatedName, annotatedFileName, "text/plain", variantsHashMap.get(TSV).getBytes(StandardCharsets.UTF_8));
        MockMultipartFile jsonFile = new MockMultipartFile(baseName, jsonFileName, "text/plain", variantsHashMap.get(JSON).getBytes(StandardCharsets.UTF_8));
        return new MockMultipartFile[]{tsvFile, jsonFile};
    }

    /**
     * Reads the uploaded file and adds an annotation pathogenic/benign
     * @param variantsFile the file that was uploaded by the user
     * @return String with annotated variants
     */
    private static HashMap<String, String> readAndAnnotateMultiPartFile(MultipartFile variantsFile) {
        BufferedReader br;
        HashMap<String, String> variantsHashMap = new HashMap<>();
        StringBuilder resultTSV = new StringBuilder("");
        StringBuilder resultJSON = new StringBuilder("");
        try {
            String line;
            InputStream is = variantsFile.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                ArrayList<String> variants = getVariants(line);
                resultTSV.append(annotateLineTSV(variants, line));
                resultJSON.append(pathogenicVariantToJSON(variants));
            }
        } catch ( IOException e ) {
            System.err.println(e.getMessage());
        }
        variantsHashMap.put(TSV, resultTSV.toString());
        variantsHashMap.put(JSON, resultJSON.toString());
        return variantsHashMap;
    }

    /**
     * Retreives possible hits for the variant in the mongo database
     * @param line, position and chromosome from the variant from the input file
     * @return Arraylist with results from the mongo db
     */
    private static ArrayList<String> getVariants(String line) {
        String[] columns = line.split("\t");
        String chromosome = columns[0];
        String position = columns[1];
        ArrayList<String> variantsList = new ArrayList<>();
        try {
            variantsList = MongoConnector.getVariantFromDatabase(chromosome, position);
        } catch ( NullPointerException ex ) {
            logger.info("No hits for: " + chromosome +" "+position);
        }
        return variantsList;
    }

    /**
     * Checks if the variant has a hit in the database, if so it is considered possible pathogenic and therefore
     * will be returned for the JSON file for further investigation
     * @param variants list with possible hits from mongo db
     * @return Either the first (only) hit or an empty string
     */
    private static String pathogenicVariantToJSON(ArrayList<String> variants) {
        if (variants.size() > 0){
            return variants.get(0);
        } else {
            return "";
        }
    }

    /**
     * Annotates a single line(variant) from the file
     * @param variants with possible hits from the database
     * @param line the line with the variant
     * @return String with a annotated variant (benign/pathogenic)
     */
    private static String annotateLineTSV(ArrayList<String> variants, String line){
        if (variants.size() == 0){
            return line+"\tbenign\n";
        } else {
            return line+"\tpathogenic\n";
        }
    }
}
