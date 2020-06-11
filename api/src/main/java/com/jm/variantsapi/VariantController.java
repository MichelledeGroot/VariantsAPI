package com.jm.variantsapi;

import com.jm.variantsapi.connector.MongoConnector;
import com.jm.variantsapi.filters.VariantsFilter;
import com.jm.variantsapi.storage.StorageException;
import com.jm.variantsapi.storage.StorageFileNotFoundException;
import com.jm.variantsapi.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.stream.Collectors;

/**
 * Controller for the API
 *
 */
@Controller
public class VariantController {

    private final StorageService storageService;
    final Logger logger = LoggerFactory.getLogger(VariantController.class);

    @Autowired
    public VariantController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(VariantController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   Model model) {

        MockMultipartFile[] resultFiles = VariantsFilter.createAnnotatedFile(file);
        storageService.store(resultFiles[0]);
        try {
            storageService.store(resultFiles[1]);
        } catch ( StorageException ex ){
            logger.warn("Empty JSON file");
        }

        model.addAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}