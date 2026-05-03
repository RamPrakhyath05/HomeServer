package com.annamareddys.homeserver.service;

import org.springframework.stereotype.Service;
// Importing other packages
import com.annamareddys.homeserver.repository.FileRepository;

// We're using a list so that we can convert a Stream data into a list for filenames
import java.util.List;
// Handling the resource that comes from the repository
import java.util.stream.Stream;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;
import java.nio.file.Path;
//Exception handling
import java.io.IOException;
import java.net.MalformedURLException;

@Service
public class FileService {
  private final FileRepository fileRepository; // Whenever we declare an instance of a class as private final, it needs
                                               // to be initialized in the constructor

  public FileService(FileRepository fileRepository) {
    this.fileRepository = fileRepository;
  }

  public List<String> listDir() throws IOException {
    Stream<Path> files = fileRepository.list();
    return files.map(Path::getFileName)
        .map(Path::toString)
        .collect(Collectors.toList());
  }

  public Resource fetchFile(String filename) throws MalformedURLException {
    return fileRepository.fetch(filename);
  }
}
