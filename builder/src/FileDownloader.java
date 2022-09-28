import java.io.*;
import java.net.*;

/**
 * Handle the download of one URL in a specific path OR the unpacking of a file towards a specific path.
 * To unpack the file the brotli dependency is needed (brotli is ~6x faster than gzip with bzip2 compression level)
 *
 * This class uses BUILD_PROXY to proxy downloads from either http or https resources.
 * BUILD_PROXY must be a special proxy because normal shitty proxies refuse syntax: GET https://....
 */
public class FileDownloader {
  private final URL mUrl;
  private final String mDestinationPath;

  public FileDownloader(String url, String destinationPath) throws IOException {
    mUrl = new URL(url);
    mDestinationPath = destinationPath;
  }

  public FileDownloader(String url) throws IOException {
    mUrl = new URL(url);
    mDestinationPath = null;
  }

  public void download() throws IOException
  {
    download(true);
  }

  public void downloadWithoutProxy() throws IOException
  {
    download(false);
  }

  private void download(boolean useProxy) throws IOException
  {
    if( mDestinationPath == null ) {
      throw new RuntimeException("You didn't specify a destinationPath");
    }
    File destinationFile = new File(mDestinationPath);

    if( destinationFile.exists() ) {
      return;
    }

    System.out.print("Downloading "+mDestinationPath+"...");

    if( !destinationFile.getParentFile().exists() ) {
      destinationFile.getParentFile().mkdirs();
    }

    try
    {
      InputStream inputStream = openStream(useProxy);
      FileOutputStream output = new FileOutputStream(mDestinationPath);
      copyStream(inputStream, output);
      System.out.println("OK");
    }
    catch (IOException ex)
    {
      System.err.println("FAILED ("+mUrl.toExternalForm()+")");
      throw ex;
    }
  }

  private void copyStream(InputStream inputStream, OutputStream output) throws IOException {
    byte[] buffer = new byte[ 64*1024 ];
    while( true )
    {
      int read = inputStream.read(buffer);
      if( read < 0 ) break;
      output.write(buffer,0,read);
    }
    output.close();
    inputStream.close();
  }

  public void downloadAndUnpack(String destinationDir) throws IOException, InterruptedException {

    if( !new File("/usr/bin/brotli").exists()  && !new File("/opt/homebrew/bin/brotli").exists() ) {
      throw new RuntimeException("brotli is not installed, run: sudo apt-get install -y brotli");
    }

    System.out.print("Download and unpacking to "+destinationDir+"...");
    InputStream inputStream = openStream(true);
    new File(destinationDir).mkdirs();

    Process process = Runtime.getRuntime().exec(
      new String[]{
        "/bin/bash",
        "-c",
        "brotli --decompress --stdout | tar xf - -C "+destinationDir
      }
    );

    copyStream(inputStream, process.getOutputStream());

    int exitValue = process.waitFor();
    if( exitValue != 0 ) {
      throw new IOException("unpack of "+mDestinationPath+" failed");
    }
    System.out.println("OK");
  }

  private InputStream openStream() throws IOException {
    HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
    connection.setRequestMethod("GET");
    connection.setConnectTimeout(1000 * 30);
    connection.setReadTimeout(1000 * 30);
    return connection.getInputStream();
  }
}
