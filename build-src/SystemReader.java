import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Jolly class:
 *   handles executing of commands and read of version,commit
 */
class SystemReader
{
  private String mCommitCache = null;
  private Version mVersionCache = null;

  public String readCommit() throws Exception {
    if( mCommitCache == null ) {
      mCommitCache = readOutputOf("git rev-parse HEAD");
    }
    return mCommitCache;
  }

  public String readOutputOf(String command) throws Exception {
    Process process = Runtime.getRuntime().exec(command);
    process.waitFor();
    if( process.exitValue() != 0 ) {
      throw new RuntimeException("Error executing "+command);
    }
    return readStream(process.getInputStream());
  }

  public void exec(String... args) throws Exception {
    Process process = Runtime.getRuntime().exec(args);
    process.waitFor();
    if( process.exitValue() != 0 ) {
      String output = readStream(process.getInputStream());
      String command = "";
      for( String arg : args ) {
        command += arg + " ";
      }
      System.out.println("Command "+command+"failed");

      if( !output.isEmpty() ) {
        System.out.println("Output:\n" + output);
      }
      String error = readStream(process.getErrorStream());
      if( !error.isEmpty() ) {
        System.out.println("Error:\n" + error);
      }
      if( error.isEmpty() && output.isEmpty() ) {
        System.out.print("No output nor error was generated from the command");
      }
      System.exit(1);
    }
  }

  public Version readVersion() throws Exception {
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
