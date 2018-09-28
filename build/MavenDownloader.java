import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MavenDownloader {
  private final List<FileDownloader> mFileDownloaders;

  public MavenDownloader(
    String destinationPath,
    String ... args
  ) throws IOException {
    if( args.length % 2 != 0 ) {
      throw new RuntimeException("Expecting 'org/example/artifact', 'version' syntax");
    }

    if( !destinationPath.endsWith("/") ) {
      destinationPath += "/";
    }
    mFileDownloaders = new ArrayList<>(args.length/2);

    for( int n=0; n < args.length; n += 2) {

      String artifactName = args[n];
      String artifactVersion = args[n+1];

      int lastSlash = artifactName.lastIndexOf('/');
      if( lastSlash == -1 ) {
        throw new RuntimeException("Expecting 'org/example/artifact', 'version' syntax");
      }

      String filename = artifactName.substring(lastSlash+1) + "-"+artifactVersion+".jar";

      mFileDownloaders.add(
        new FileDownloader(
          "http://central.maven.org/maven2/"+artifactName+"/"+artifactVersion+"/"+filename,
          destinationPath+filename
        )
      );
    }
  }

  public void download() throws IOException {
    for( FileDownloader fileDownloader : mFileDownloaders ) {
      fileDownloader.download();
    }
  }
}
