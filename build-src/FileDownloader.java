import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

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
    if( mDestinationPath == null ) {
      throw new RuntimeException("You didn't specify a destinationPath");
    }
    File destinationFile = new File(mDestinationPath);

    System.out.print("Downloading "+mDestinationPath+"...");
    if( destinationFile.exists() ) {
      System.out.println("already exists, skipping.");
      return;
    }

    if( !destinationFile.getParentFile().exists() ) {
      destinationFile.getParentFile().mkdirs();
    }

    InputStream inputStream = openStream();
    FileOutputStream output = new FileOutputStream(mDestinationPath);
    copyStream(inputStream, output);
    System.out.println("OK");
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
    System.out.print("Download and unpacking to "+destinationDir+"...");
    InputStream inputStream = openStream();
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
    URL url;
    String buildProxyEnv = System.getenv().get("BUILD_PROXY");
    if( buildProxyEnv != null && !buildProxyEnv.isEmpty()) {
      System.out.print("(Using Proxy)...");
      URL proxyUrl = new URL(buildProxyEnv);
      url = new URL(proxyUrl.getProtocol(), proxyUrl.getHost(), proxyUrl.getPort(), mUrl.toExternalForm());
    }
    else {
      System.out.print("(Direct)...");
      url = mUrl;
    }

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setConnectTimeout(1000 * 30);
    connection.setReadTimeout(1000 * 30);
    return connection.getInputStream();
  }
}
