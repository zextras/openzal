
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.tools.*;

@SuppressWarnings("ALL")
public class Build
{

  private final JavaCompiler mJavaCompiler;
  private final FileManager mFileManager;
  private final SourcePreprocessor mSourcePreprocessor;
  private final List<String> mLibDirectories;

  public Build( List<String> libDirectories, String destinationJar, SourcePreprocessor sourcePreprocessor ) {
    mLibDirectories = libDirectories;
    mJavaCompiler = ToolProvider.getSystemJavaCompiler();
    mFileManager = new FileManager(
      mJavaCompiler.getStandardFileManager(null, Locale.US, StandardCharsets.UTF_8),
      destinationJar
    );
    mSourcePreprocessor = sourcePreprocessor;
  }

  void compileAll() throws Exception
  {
    List<String> options = new LinkedList<>();
    options.add("-g");
    options.add("-target");
    options.add("1.7");
    options.add("-source");
    options.add("1.7");
    options.add("-encoding");
    options.add("utf-8");
    options.add("-classpath");
    options.add(listJars());

   /* for( String option : options )
    {
      System.out.println("option: "+option);
    }
*/
    List<? extends JavaFileObject> compilationUnitsList = listSources();

    JavaCompiler.CompilationTask task = mJavaCompiler.getTask(
      null, /* out - a Writer for additional output from the compiler; use System.err if null */
      mFileManager, /* fileManager - a file manager; if null use the compiler's standard filemanager */
      null, /* diagnosticListener - a diagnostic listener; if null use the compiler's default method for reporting diagnostics */
      options,
      null, /* classes - names of classes to be processed by annotation processing, null means no class names */
      compilationUnitsList /* compilationUnits - the compilation units to compile, null means no compilation units */
    );

    boolean result = task.call();
    if( result )
    {
      System.err.println("Compilation successful");
    }
    else
    {
      System.err.println("Compilation failed");
      System.exit(1);
    }

    mFileManager.close();
  }

  private String listJars() throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    for( String path : mLibDirectories ) {
      listJars(new File(path), stringBuffer);
    }
    return stringBuffer.substring(0, stringBuffer.length()-1 );
  }

  private void listJars(File directory, StringBuffer stringBuffer) throws IOException {
    String filenameList[] = directory.list();
    if (filenameList == null) filenameList = new String[0];

    for (String filename : filenameList) {
      final File subFile = new File(directory, filename);
      if (subFile.isDirectory()) {
        listJars(subFile, stringBuffer);
      } else {
        stringBuffer.append(subFile.getAbsolutePath()).append(':');
      }
    }
  }

  private List<? extends JavaFileObject> listSources() throws IOException {
    return listSources("src/java/", "");
  }

  private List<? extends JavaFileObject> listSources(String sourcePath, String subPath) throws IOException {
    LinkedList<JavaFileObject> list = new LinkedList<>();

    File directory = new File(sourcePath,subPath);
    String filenameList[] = directory.list();
    if( filenameList == null ) filenameList = new String[0];

    for (String filename : filenameList) {
      final File subFile = new File(directory, filename);
      if (subFile.isDirectory()) {
        list.addAll(listSources(sourcePath, subPath+filename+'/'));
      } else {
        JavaFileObject obj = new SourceCode(
          subFile,
          mSourcePreprocessor,
          JavaFileObject.Kind.SOURCE
        );
        list.add( obj );
      }
    }

    return list;
  }
}
