
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.tools.*;

/**
 * This class builds java source code given:
 *   java version used
 *   library paths
 *   source directories
 *   resource direcories
 *   libraries to embed, addictional regex to embed files from libraries
 *   ignore rules when files do not match embedding rules
 *   destination jar path
 *   SourcePreprocessor (EmptySourcePreprocessor available)
 *   manifest map
 *
 * This class has the responsibility to:
 *   - validating provided paths
 *   - compile java
 *   - embed resources
 *   - embed libraries with the provided rules
 *   - execute source preprocessor
 *   - write the manifest file
 *
 * This class can be called concurrently with other builds, by default the stdout/sdterr of compiler is hidden only in
 * case of failure the compile is re-run and output is shown.
 *
 * System.exit(1) is used when a build fails.
 */
@SuppressWarnings("ALL")
public class Build
{
  private final Pattern             sStandardPattern = Pattern.compile(
    ".*[.](class|xml|properties|xsd|xsb|ngp|afm|types|sql|text|json|so|jnilib)"
  );

  private final JavaCompiler        mJavaCompiler;
  private final SourcePreprocessor  mSourcePreprocessor;
  private final JavaVersion         mJavaVersion;
  private final List<String>        mLibDirectories;
  private final List<String>        mSourceDirectories;
  private final List<String>        mResourceDirectories;
  private final List<String>        mEmbeddedLibDirectories;
  private final List<String>        mExtraEmbeddingRules;
  private final List<String>        mIgnoreLibFilesList;
  private final String              mDestinationJar;
  private final Map<String, String> mManifest;
  private final FileManager.Renamer mRenamer;
  private       FileManager         mFileManager;

  enum JavaVersion
  {
    Java6("6", 50),
    Java7("7", 51),
    Java8("8", 52),
    Java9("9", 53),
    Java10("10", 54),
    Java11("11", 55),
    Java12("12", 56);

    private final String version;
    private final int versionCode;

    JavaVersion(String version, int versionCode)
    {
      this.version = version;
      this.versionCode = versionCode;
    }
  }

  public Build(
    JavaVersion javaVersion,
    List<String> libDirectories,
    List<String> sourceDirectories,
    List<String> resourceDirectories,
    List<String> embeddedLibDirectories,
    List<String> extraEmbeddingRules,
    List<String> ignoreLibFilesList,
    String destinationJar,
    SourcePreprocessor sourcePreprocessor,
    Map<String, String> manifest
  )
  {
    this(
      javaVersion,
      libDirectories,
      sourceDirectories,
      resourceDirectories,
      embeddedLibDirectories,
      extraEmbeddingRules,
      ignoreLibFilesList,
      destinationJar,
      sourcePreprocessor,
      manifest,
      new FileManager.Renamer()
      {
        @Override
        public String rename(String path)
        {
          return path;
        }
      }
    );
  }

  public Build(
    JavaVersion javaVersion,
    List<String> libDirectories,
    List<String> sourceDirectories,
    List<String> resourceDirectories,
    List<String> embeddedLibDirectories,
    List<String> extraEmbeddingRules,
    List<String> ignoreLibFilesList,
    String destinationJar,
    SourcePreprocessor sourcePreprocessor,
    Map<String, String> manifest,
    FileManager.Renamer renamer
  )
  {
    mJavaVersion = javaVersion;
    mLibDirectories = libDirectories;
    mSourceDirectories = sourceDirectories;
    mResourceDirectories = resourceDirectories;
    mEmbeddedLibDirectories = embeddedLibDirectories;
    mExtraEmbeddingRules = extraEmbeddingRules;
    mIgnoreLibFilesList = ignoreLibFilesList;
    mDestinationJar = destinationJar;
    mManifest = manifest;
    mRenamer = renamer;
    mJavaCompiler = ToolProvider.getSystemJavaCompiler();
    mFileManager = createFileManager();
    mSourcePreprocessor = sourcePreprocessor;

    validatePaths();
  }

  private final FileManager createFileManager()
  {
    return new FileManager(
      mJavaCompiler.getStandardFileManager(null, Locale.US, StandardCharsets.UTF_8),
      mDestinationJar,
      mRenamer
    );
  }

  private void validatePaths()
  {
    ArrayList<String> directories = new ArrayList<>(1024);

    directories.addAll(mLibDirectories);
    directories.addAll(mSourceDirectories);
    directories.addAll(mEmbeddedLibDirectories);
    directories.addAll(mResourceDirectories);

    for( String path : directories)
    {
      if( !new File(path).exists() ) {
        throw new RuntimeException("Path '"+path+"' doesn't exist");
      }

      if( path.startsWith("/") ) {
        throw new RuntimeException("Path '"+path+"' is absolute! convert to relative");
      }
    }
  }

  void compileAll(String text) throws Exception
  {
    List<String> options = new LinkedList<>();
    options.add("-g");
    options.add("-target");
    options.add(mJavaVersion.version);
    options.add("-source");
    options.add(mJavaVersion.version);
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

    System.out.println(text+" Compilation...");
    boolean result = task.call();
    if( result )
    {
      System.out.println(text+" Compilation...OK");
    }
    else
    {
      System.err.println(text+" Compilation...FAILED");
      mFileManager.close();
      mFileManager = createFileManager();
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

    mFileManager.writeFile("META-INF/MANIFEST.MF",createManifestStream(mManifest));

    if( !mResourceDirectories.isEmpty() )
    {
      System.out.println(text+" Copying Resources...");
      try
      {
        copyResources(mFileManager);
      }
      catch (Exception ex)
      {
        System.err.println(text+" Copying Resources...FAILED");
        throw ex;
      }
      System.out.println(text+" Copying Resources...OK");
    }

    try
    {
      if( !mEmbeddedLibDirectories.isEmpty() )
      {
        System.out.println(text+" Embedding Libraries...");
        boolean embedLibrariesResult = embedLibraries(mFileManager);
        if (embedLibrariesResult)
        {
          System.out.println(text + " Embedding Libraries...OK");
        }
        else
        {
          System.err.println(text + " Embedding Libraries...FAILED");
          System.err.println("One or more files are unmatched by regex nor present in the ignore list, add them");
          System.exit(1);
        }
      }
    }
    catch (Exception ex)
    {
      System.err.println(text+" Embedding libraries...FAILED");
      throw ex;
    }
    finally
    {
      mFileManager.close();
    }

    ClassVersionChecker.checkJar(mJavaVersion.versionCode, new File(mDestinationJar));
  }

  private void copyResources(FileManager fileManager)
    throws Exception
  {
    for( String path : mResourceDirectories )
    {
      copyResources(new File(path), new File(path), fileManager);
    }
  }

  private void copyResources(File path, File root, FileManager fileManager)
    throws Exception
  {
    if( path.isFile() ) {
      int length = root.getPath().length()+1;
      fileManager.writeFile(path.getPath().substring(length), new FileInputStream(path) );
      return;
    }

    String filenameList[] = path.list();
    if (filenameList == null) filenameList = new String[0];

    for (String filename : filenameList) {
      copyResources(new File(path, filename), root, fileManager);
    }
  }

  private boolean embedLibraries(FileManager fileManager)
    throws Exception
  {
    boolean result = true;

    for( String path : mEmbeddedLibDirectories )
    {
      result = embedLibraries(new File(path), fileManager) && result;
    }

    return result;
  }

  private boolean embedLibraries(File directory, FileManager fileManager)
    throws Exception
  {
    if( directory.isFile() ) {
      return embedLibrary(directory, fileManager);
    }

    boolean result = true;

    String filenameList[] = directory.list();
    if (filenameList == null) filenameList = new String[0];

    for (String filename : filenameList) {
      final File subFile = new File(directory, filename);
      result = embedLibraries(subFile, fileManager) && result;
    }

    return result;
  }

  private boolean embedLibrary(File jar, FileManager fileManager)
    throws Exception
  {
    boolean result = true;

    try
    {
      ZipFile zipFile = new ZipFile(jar);
      Set<String> deduplicatorForThisJar = new HashSet<>();

      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements())
      {
        ZipEntry entry = entries.nextElement();
        if (!entry.isDirectory())
        {
          String name = entry.getName();
          boolean matched = sStandardPattern.matcher(name).matches();

          for( String extraRule : mExtraEmbeddingRules )
          {
            matched = matched || Pattern.matches(extraRule, name);
          }

          if (matched)
          {
            if( deduplicatorForThisJar.add(name) )
            {
              fileManager.writeFile(name, zipFile.getInputStream(entry));
            }
          }
          else
          {
            for( String ignoreRule : mIgnoreLibFilesList )
            {
              matched = Pattern.matches(ignoreRule, name);
              if( matched ) break;
            }

            if (!matched)
            {
              System.out.println("Unmatched file [" + jar.getName() + "]: " + name);
              result = false;
            }
          }
        }
      }

      zipFile.close();
      return result;
    }
    catch (IOException ex)
    {
      System.out.println("Exception when trying to add "+jar);
      throw ex;
    }
  }

  public static InputStream createManifestStream(Map<String,String> manifest) {
    StringBuffer stringBuffer = new StringBuffer();

    stringBuffer.append("Manifest-Version: 1.0\r\n");
    for( Map.Entry<String,String> entry : manifest.entrySet() ) {
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
    if( directory.isFile() ) {
      stringBuffer.append(directory.getAbsolutePath()).append(':');
      return;
    }

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
