import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

class SystemReader
{
  private String mCommitCache = null;
  private Version mVersionCache = null;

  public String readCommit() throws IOException, InterruptedException {
    if( mCommitCache == null ) {
      mCommitCache = readOutputOf("git rev-parse HEAD");
    }
    return mCommitCache;
  }

  public String readOutputOf(String command) throws IOException, InterruptedException {
    Process process = Runtime.getRuntime().exec("git rev-parse HEAD");
    process.waitFor();
    if( process.exitValue() != 0 ) {
      throw new RuntimeException("Error executing "+command);
    }
    return readStream(process.getInputStream());
  }

  public Version readVersion() throws IOException {
    if( mVersionCache == null ) {
      mVersionCache = new Version(
        readStream(new FileInputStream(new File("version")))
      );
    }
    return mVersionCache;
  }

  private String readStream(InputStream stream) throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    while( true ) {
      byte[] buffer = new byte[64*1024];
      int read = stream.read(buffer);
      if( read > 0 ) stringBuffer.append(
        new String(buffer,0,read-1,"UTF-8")
      );

      if( read < 0 ) break;
    }
    return stringBuffer.toString();
  }
}
