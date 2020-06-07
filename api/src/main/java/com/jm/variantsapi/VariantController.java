package com.jm.variantsapi;

import com.jm.variantsapi.filters.VariantsFilter;
import com.jm.variantsapi.storage.StorageFileNotFoundException;
import com.jm.variantsapi.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.util.stream.Collectors;

/**
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
 **/
@Controller
public class VariantController {

    private final StorageService storageService;

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

        //MultipartFile annotatedFile = VariantsFilter.createAnnotatedFile(file);
        storageService.store(VariantsFilter.createAnnotatedFile(file));
        model.addAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}