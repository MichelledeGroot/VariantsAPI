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

/**
 * This class is responsible for reading the uploaded file and creating a annotated file
 * The annotation is either benign when there are no hits in the possible pathogenic varianst database
 * or pathogenic when the variant can be found in the possible pathogenic database
 *
 */
public class VariantsFilter {

    static final Logger logger = LoggerFactory.getLogger(VariantsFilter.class);

    /**
     * Uses the uploaded file to create a new annotated file
     * @param variantsFile the file that was uploaded by the user
     * @return MockMultipartFile to be stored
     */
    public static MultipartFile createAnnotatedFile(MultipartFile variantsFile){
        logger.info("Reading file: "+variantsFile.getOriginalFilename());
        String variantsList = readAndAnnotateMultiPartFile(variantsFile);
        String annotatedName = FilenameUtils.removeExtension(variantsFile.getOriginalFilename()) + "_annotated";
        String annotatedFileName = annotatedName+".tsv";
        logger.info("Wrote annotations to: " + annotatedFileName);
        return new MockMultipartFile(annotatedName, annotatedFileName, "text/plain", variantsList.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Reads the uploaded file and adds an annotation pathogenic/benign
     * @param variantsFile the file that was uploaded by the user
     * @return String with annotated variants
     */
    private static String readAndAnnotateMultiPartFile(MultipartFile variantsFile) {
        BufferedReader br;
        StringBuilder result = new StringBuilder("");
        try {
            String line;
            InputStream is = variantsFile.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                result.append(annotateLine(line));
            }
        } catch ( IOException e ) {
            System.err.println(e.getMessage());
        }
        return result.toString();
    }

    /**
     * Annotates a single line(variant) from the file
     * @param line the line with the variant
     * @return String with a annotated variant (benign/pathogenic)
     */
    private static String annotateLine(String line){
        String[] columns = line.split("\t");
        String chromosome = columns[0];
        String position = columns[1];
        ArrayList<String> variantsList = MongoConnector.getVariantFromDatabase(chromosome, position);
        //logger.info(variantsList.toString());
        if (variantsList.size() == 0){
            return line+"\tbenign\n";
        } else {
            return line+"\tpathogenic\n";
        }
    }
}
