import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Download maven artifacts in the specified path and removed unknown libraries from the destination path,
 * The args must be populated with library name and version such as:
 *   "org/example/name/artifact", "1.2.3/variant"
 */
public class MavenDownloader {
  private final List<FileDownloader> mFileDownloaders;
  private final HashSet<String>      mValidFilenames;
  private final String               mDestinationPath;

  public MavenDownloader(
    String destinationPath,
    String ... args
  ) throws IOException
  {
    if( args.length % 2 != 0 ) {
      throw new RuntimeException("Expecting 'org/example/artifact', 'version' syntax");
    }

    mDestinationPath = destinationPath;
    mValidFilenames = new HashSet<>();

    if( !destinationPath.endsWith("/") ) {
      destinationPath += "/";
    }
    mFileDownloaders = new ArrayList<>(args.length/2);

    for( int n=0; n < args.length; n += 2) {

      String[] splittedVersion = args[n + 1].split("/");

      String artifactName = args[n];
      String artifactVersion = splittedVersion[0];
      String artifactVariant = splittedVersion.length > 1 ? "-"+splittedVersion[1] : "";

      int lastSlash = artifactName.lastIndexOf('/');
      if( lastSlash == -1 ) {
        throw new RuntimeException("Expecting 'org/example/artifact', 'version' syntax");
      }

      String filename = artifactName.substring(lastSlash+1) + "-"+artifactVersion+artifactVariant+".jar";
      mValidFilenames.add(filename);

      mFileDownloaders.add(
        new FileDownloader(
          "https://repo.maven.apache.org/maven2/"+artifactName+"/"+artifactVersion+"/"+filename,
          destinationPath+filename
        )
      );
    }
  }

  private void removeUnknownLibraries()
  {
    File destinationDir = new File(mDestinationPath);

    String[] entries = destinationDir.list();
    if( entries == null ) entries = new String[0];

    for( String entry : entries )
    {
      File file = new File(destinationDir, entry);

      if(!mValidFilenames.contains(file.getName()) )
      {
        if( file.getName().endsWith(".jar") )
        {
          System.out.println("Removing " + file.getName());
          file.delete();
        }
        else
        {
          throw new RuntimeException("Unknown file "+file.getAbsolutePath());
        }
      }
    }
  }

  public void download() throws IOException
  {
    removeUnknownLibraries();

    for( FileDownloader fileDownloader : mFileDownloaders ) {
      fileDownloader.download();
    }
  }
}
