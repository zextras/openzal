import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FileDownloader {
  private final URL mUrl;
  private final String mDestinationPath;

  public FileDownloader(String url, String destinationPath) throws IOException {
    mUrl = new URL(url);
    mDestinationPath = destinationPath;
  }

  public void download() throws IOException
  {
    File destinationFile = new File(mDestinationPath);

    System.out.print("Downloading "+mDestinationPath+"...");
    if( destinationFile.exists() ) {
      System.out.println("already exists, skipping.");
      return;
    }

    if( !destinationFile.getParentFile().exists() ) {
      destinationFile.getParentFile().mkdirs();
    }

    byte[] buffer = new byte[ 64*1024 ];
    InputStream inputStream = mUrl.openStream();
    FileOutputStream output = new FileOutputStream(mDestinationPath);
    while( true )
    {
      int read = inputStream.read(buffer);
      if( read < 0 ) break;
      output.write(buffer,0,read);
    }
    output.close();
    inputStream.close();
    System.out.println("Done");
  }

  public void unpack(String destinationDir) throws IOException, InterruptedException {
    System.out.print("Unpacking to "+destinationDir+"...");
    new File(destinationDir).mkdirs();
    Process process = Runtime.getRuntime().exec(
      new String[]{
        "tar",
        "xf",
        mDestinationPath,
        "-C",
        destinationDir
      }
    );

    int exitValue = process.waitFor();
    if( exitValue != 0 ) {
      throw new IOException("unpack of "+mDestinationPath+" failed");
    }
  }
}
