package com.annamareddys.homeserver.repository;

import org.springframework.stereotype.Repository;

// The packages that are needed for handling path
import java.nio.file.Path;
import java.nio.file.Paths;
// To do file operations 
import java.util.stream.Stream;
import java.nio.file.Files;
// Handling resources in spring boot so that we can return the files to the frontend
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
// Handling exceptions
import java.io.IOException;
import java.net.MalformedURLException;

@Repository
public class FileRepository {
  Path dir = Paths.get("/homeserver-files");

  // To List all the files that exist in the directory
  public Stream<Path> list() throws IOException { // Files.list automatically throws an IOException so we need to handle
                                                  // it
    return Files.list(dir);
  }

  // To fetch a specific file based on the filename
  public Resource fetch(String filename) throws MalformedURLException { // Same goes for resource, resource throws a
                                                                        // MalformedURLException
    Path filepath = dir.resolve(filename);
    Resource resource = new UrlResource(filepath.toUri());
    if (!resource.exists() || !resource.isReadable()) {
      return null;
    }
    return resource;
  }
}
