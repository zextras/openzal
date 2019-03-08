import javax.tools.SimpleJavaFileObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Read source code and apply source preprocessor just in time
 */
class SourceCode extends SimpleJavaFileObject
{
  private final File mFile;
  private final SourcePreprocessor mSourcePreprocessor;

  SourceCode(File file, SourcePreprocessor sourcePreprocessor, Kind kind) {
    super(file.toURI(), kind);
    mFile = file;
    mSourcePreprocessor = sourcePreprocessor;
  }

  @Override
  public CharSequence getCharContent(boolean b) throws IOException {
    FileInputStream is = new FileInputStream(mFile);
    StringBuffer stringBuffer = new StringBuffer();
    while( true )
    {
      byte[] buffer = new byte[ 1024*64 ];
      int read = is.read(buffer);
      if( read > 0 ) {
        stringBuffer.append( new String(buffer,0,read, "UTF-8") );
      }
      if( read < 0 ) break;
    }

    try {
      mSourcePreprocessor.apply(
        stringBuffer
      );

      if( false )
      {
        writeToDebugPreprocessor(
          mFile,
          stringBuffer
        );
      }
      return stringBuffer;
    }
    catch (Exception ex)
    {
      System.err.println("== Preprocessor Error ==");
      System.err.println(mFile.getPath()+":"+ex.toString());
      System.exit(1);
      return null;
    }
  }

  private void writeToDebugPreprocessor(File file, StringBuffer stringBuffer) throws IOException {
    File dstFile = new File("/tmp/pre-debug/", file.getPath());
    dstFile.getParentFile().mkdirs();
    FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
    fileOutputStream.write(stringBuffer.toString().getBytes("UTF-8"));
    fileOutputStream.close();
  }
}
