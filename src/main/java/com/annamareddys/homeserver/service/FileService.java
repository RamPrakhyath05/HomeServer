// Importing the repository into service
import com.annamareddys.homeserver.repository.FileRepository;

// Spring Framework Stereotype import
import org.springframework.stereotype.Service;

// Data Type imports
import java.util.List;

// Handling exceptions from Repository
import java.io.IOException;
import java.net.MalformedURLException;

// Resource module import, because the repository returns a resource
import org.springframework.core.io.Resource;

// Handling the sequence of data and path
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.nio.file.Path;


@Service
public class FileService{
  private final FileRepository fileRepository;
  FileService(FileRepository fileRepository){
    this.fileRepository = fileRepository;
  }
  
  public List<String> listFiles() throws IOException{
    Stream<Path> fileNameStream = fileRepository.list();
    return fileNameStream.map(Path::getFileName).map(Path::toString).collect(Collectors.toList());
  }

  public Resource fetchFile(String filename) throws MalformedURLException{
    return fileRepository.fetch(filename);
  }
}
