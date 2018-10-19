
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.tools.*;

/**
 * This class builds java source code given:
 *   library paths, source directories, jar path, SourcePreprocessor (EmptySourcePreprocessor available) and manifest
 *
 * This class can be called concurrently with other builds, by default the stdout/sdterr of compiler is hidden only in
 * case of failure the compile is re-run and output is shown.
 *
 * System.exit(1) is used when a build fails.
 */
@SuppressWarnings("ALL")
public class Build
{
  private final JavaCompiler mJavaCompiler;
  private final FileManager mFileManager;
  private final SourcePreprocessor mSourcePreprocessor;
  private final List<String> mLibDirectories;
  private final List<String> mSourceDirectories;
  private final Map<String, String> mManifest;

  public Build(
    List<String> libDirectories,
    List<String> sourceDirectories,
    String destinationJar,
    SourcePreprocessor sourcePreprocessor,
    Map<String, String> manifest
  )
  {
    mLibDirectories = libDirectories;
    mSourceDirectories = sourceDirectories;
    mManifest = manifest;
    mJavaCompiler = ToolProvider.getSystemJavaCompiler();
    mFileManager = new FileManager(
      mJavaCompiler.getStandardFileManager(null, Locale.US, StandardCharsets.UTF_8),
      destinationJar
    );
    mSourcePreprocessor = sourcePreprocessor;
  }

  void compileAll(String text) throws Exception
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

    List<? extends JavaFileObject> compilationUnitsList = listSources();
    DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

    JavaCompiler.CompilationTask task = mJavaCompiler.getTask(
      null, /* out - a Writer for additional output from the compiler; use System.err if null */
      mFileManager, /* fileManager - a file manager; if null use the compiler's standard filemanager */
      diagnosticCollector, /* diagnosticListener - a diagnostic listener; if null use the compiler's default method for reporting diagnostics */
      options,
      null, /* classes - names of classes to be processed by annotation processing, null means no class names */
      compilationUnitsList /* compilationUnits - the compilation units to compile, null means no compilation units */
    );

    System.out.println(text);
    boolean result = task.call();
    if( result )
    {
      System.err.println(text+"OK");
    }
    else
    {
      System.err.println(text+"FAILED");
      task = mJavaCompiler.getTask(
        null, /* out - a Writer for additional output from the compiler; use System.err if null */
        mFileManager, /* fileManager - a file manager; if null use the compiler's standard filemanager */
        null, /* diagnosticListener - a diagnostic listener; if null use the compiler's default method for reporting diagnostics */
        options,
        null, /* classes - names of classes to be processed by annotation processing, null means no class names */
        compilationUnitsList /* compilationUnits - the compilation units to compile, null means no compilation units */
      );
      task.call();
      System.exit(1);
    }

    mFileManager.writeFile("META-INF/MANIFEST.MF",createManifestStream());
    mFileManager.close();
  }

  private InputStream createManifestStream() {
    StringBuffer stringBuffer = new StringBuffer();

    stringBuffer.append("Manifest-Version: 1.0\r\n");
    for( Map.Entry<String,String> entry : mManifest.entrySet() ) {
      stringBuffer.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
    }
    stringBuffer.append("\r\n");

    return new ByteArrayInputStream(
      stringBuffer.toString().getBytes(StandardCharsets.UTF_8)
    );
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
    ArrayList<JavaFileObject> sourceFiles = new ArrayList<>(4096);
    for( String sourcePath : mSourceDirectories) {
      sourceFiles.addAll(
        listSources(sourcePath, "")
      );
    }
    return sourceFiles;
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
