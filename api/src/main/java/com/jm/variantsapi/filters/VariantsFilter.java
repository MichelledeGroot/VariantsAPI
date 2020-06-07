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
import java.util.List;

public class VariantsFilter {

    static final Logger logger = LoggerFactory.getLogger(VariantsFilter.class);

    public static MultipartFile createAnnotatedFile(MultipartFile variantsFile){
        logger.info("Reading file: "+variantsFile.getOriginalFilename());
        String variantsList = readAndAnnotateMultiPartFile(variantsFile);
        String annotatedName = FilenameUtils.removeExtension(variantsFile.getOriginalFilename()) + "_annotated";
        String annotatedFileName = annotatedName+".tsv";
        logger.info("Wrote annotations to: " + annotatedFileName);
        return new MockMultipartFile(annotatedName, annotatedFileName, "text/plain", variantsList.getBytes(StandardCharsets.UTF_8));
    }

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

    private static String annotateLine(String line){
        String[] columns = line.split("\t");
        String chromosome = columns[0];
        String position = columns[1];
        ArrayList<String> variantsList = MongoConnector.getVariantFromDatabase(chromosome, position);
        System.out.println(variantsList.size());
        if (variantsList.size() == 0){
            return line+"\tbenign\n";
        } else {
            return line+"\tpathogenic\n";
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
