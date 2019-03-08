import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Jolly class:
 *   handles executing of commands and read of version,commit
 */
class SystemReader
{
  private String mCommitCache = null;
  private Version mVersionCache = null;
  private Version mZalVersionCache = null;

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

  public void execWithOutput(String name, String command)
    throws Exception
  {
    System.out.println("Running "+name+"...");

    ProcessBuilder builder = new ProcessBuilder(
      "/bin/bash",
      "-c",
      command
    );

    builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
    builder.redirectError(ProcessBuilder.Redirect.INHERIT);
    builder.redirectInput(ProcessBuilder.Redirect.INHERIT);

    int result = builder.start().waitFor();
    if( result != 0 ) {
      System.err.println("Running "+name+"...FAILED");
      throw new RuntimeException("Running "+name+" Failed");
    }

    System.out.println("Running "+name+"...OK");
  }

  public Version readVersion() throws Exception {
    if( mVersionCache == null ) {
      mVersionCache = new Version(
        readFile("version")
      );
    }
    return mVersionCache;
  }

  public String readFile(String path) throws Exception
  {
    return readStream(new FileInputStream(new File(path)));
  }

  private String readStream(InputStream stream) throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    while( true ) {
      byte[] buffer = new byte[64*1024];
      int read = stream.read(buffer);
      if( read > 0 ) stringBuffer.append(
        new String(buffer, 0, read-1, StandardCharsets.UTF_8)
      );

      if( read < 0 ) break;
    }
    return stringBuffer.toString();
  }
}
