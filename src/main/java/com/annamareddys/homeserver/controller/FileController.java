package com.annamareddys.homeserver.controller; 
// Controller talks to the service, so we need to import service
import com.annamareddys.homeserver.service.FileService;

// Data structure imports
import java.util.List;

// Spring Framework Stereotype import
import org.springframework.web.bind.annotation.RestController;

// Handle uploading file
import org.springframework.web.multipart.MultipartFile;

// Request Mapping and Get Mapping imports
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Handling the resource that comes from the service
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

// Exception handling
import java.io.IOException;
import java.net.MalformedURLException;

// Enabling Cross Origin
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("")
public class FileController{
  private final FileService fileService;
  FileController(FileService fileService){
    this.fileService = fileService;
  }
  
  @GetMapping("/alive")
  public String checkHealth(){
    return "All is well :)";
  }

  @GetMapping("/files")
  public List<String> getAllFiles() throws IOException{
    return fileService.listFiles();
  }

  @GetMapping("/files/{fileName}")
  public ResponseEntity<Resource> getFile(@PathVariable String fileName) throws MalformedURLException{
    Resource resource = fileService.fetchFile(fileName);
    if (resource==null){
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(resource);
  }
  
  @PostMapping("/files/upload")
  public void saveFile(@RequestParam MultipartFile file) throws IOException{
    fileService.storeFile(file);  
  }

  @DeleteMapping("/files/{fileName}")
  public void removeFile(@PathVariable String fileName) throws IOException {
    fileService.deleteFile(fileName);
  }

}

