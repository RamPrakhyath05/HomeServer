package com.annamareddys.homeserver.controller;

// Data structure imports
import java.util.List;

// Importing essentials for a controller
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Importing service to talk to repository
import com.annamareddys.homeserver.service.FileService;

// Handling exceptions from service
import java.io.IOException;
import java.net.MalformedURLException;

// Since the resource won't be a downloadble file directly, we need to wrap it in a ResponseEntity with proper headers
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("")

public class FileController {
    private final FileService fileService;

    FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/alive")
    public String checkIfAlive() {
        return "All is well :)";
    }

    // To list all the existing files on the server
    @GetMapping("/files")
    public List<String> getDir() throws IOException {
        return fileService.listDir();
    }

    // To get a specific file using the filename
    @GetMapping("/files/{filename}") // Path variable, so that the url can directly contain the filename
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws MalformedURLException {
        Resource resource = fileService.fetchFile(filename);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
