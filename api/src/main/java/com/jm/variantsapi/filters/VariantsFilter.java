package com.jm.variantsapi.filters;

import com.jm.variantsapi.connector.MongoConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner; // Import the Scanner class to read text files

public class VariantsFilter {

    static final Logger logger = LoggerFactory.getLogger(VariantsFilter.class);

    public static MultipartFile createAnnotatedFile(MultipartFile variantsFile){
        try {
            logger.info("Reading file: "+variantsFile.getOriginalFilename());
            String annotatedFileName = FilenameUtils.removeExtension(variantsFile.getOriginalFilename()) + "_annotated.tsv";
            File annotatedFile = new File(annotatedFileName);
            annotatedFile.createNewFile();
            Scanner myReader = new Scanner(convertMultipartToFile(variantsFile));
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                System.out.println(line);
                String annotatedLine = annotateLine(line);
                Files.write(Paths.get(String.valueOf(annotatedFile)), annotatedLine.getBytes(), StandardOpenOption.APPEND);
            }
            myReader.close();
            logger.info("Wrote annotations to: " + annotatedFileName);
            return new MockMultipartFile(annotatedFileName, new FileInputStream(annotatedFile));
        } catch ( IOException e ) {
            logger.error("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }

    private static String annotateLine(String line){
        String[] columns = line.split("\t");
        String chromosome = columns[0];
        String position = columns[1];
        ArrayList<String> variantsList = MongoConnector.getVariantFromDatabase(chromosome, position);
        System.out.println(variantsList.size());
        if (variantsList.size() == 0){
            return line+"\tbenign";
        } else {
            return line+"\tpathogenic";
        }
    }

    private static File convertMultipartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
