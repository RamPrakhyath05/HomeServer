package com.annamareddys.homeserver.repository; 
// Spring Framework Stereotype import
import org.springframework.stereotype.Repository;

// Importing necessary modules
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.springframework.web.multipart.MultipartFile;

// Files.list(dir) will basically return a sequence of elements, hence we need Stream<Path> 
import java.util.stream.Stream;

// Files.list(dir) also throws a checked IOException so we need to handle that
import java.io.IOException;
// URLResource can throw a MalformedURLException, hence:
import java.net.MalformedURLException;

// Whatever file exists in the directory, if we try to fetch it, it gets returned as a resource
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@Repository
public class FileRepository{
  Path dir = Paths.get("/homeserver-files");
  
  public Stream<Path> list() throws IOException{
    return Files.list(dir);
  } 
  
  public Resource fetch(String filename) throws MalformedURLException{
    UrlResource resource = new UrlResource(dir.resolve(filename).toUri()); 
    if (!resource.exists() || !resource.isReadable()) {
      return null;
    }
    return resource;
  }

  public void store(MultipartFile file) throws IOException{
    Path target = dir.resolve(file.getOriginalFilename());
    Files.copy(file.getInputStream(), target);
  }

}

