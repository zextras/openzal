import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ZalBuilder
{
  private static final int sMaxConcurrentTask = 4;

  private static final Version sFirstSupportedZimbraVersion = new Version("8.0.0");
  private static final Version sLastSupportedZimbraVersion  = new Version("9.0.0");
  private static final Zimbra  sZimbraX          = new Zimbra(
    Zimbra.Type.x,
    new Version("8.8.15")
  );

  private static AtomicBoolean sCheckedDependencies = new AtomicBoolean(false);
  private static AtomicBoolean sSetupPerformed  = new AtomicBoolean(false);
  private static AtomicBoolean sGitCheck = new AtomicBoolean(true);
  private static AtomicBoolean sSkipLast = new AtomicBoolean(false);

  private static List<Zimbra> sCommonZimbraVersions = Arrays.asList(new Zimbra[]{
    new Zimbra(Zimbra.Type.standard, new Version("8.6.0")),
    new Zimbra(Zimbra.Type.standard, new Version("8.7.11")),
    new Zimbra(Zimbra.Type.standard, new Version("8.8.12")),
    new Zimbra(Zimbra.Type.standard, new Version("8.8.15")),
    sZimbraX
  });

  private static List<String> sSkipDotClass = Arrays.asList(
    "com/zimbra/cs/store/file/VolumeBlobProxy.class"
  );

  public static void main(String args[])
    throws Exception
  {
    setupStdoutout();

    SystemReader systemReader = new SystemReader();

    System.out.println("ZAL - Version "+systemReader.readVersion());
    if( args.length == 0 ) {
      help();
      System.exit(1);
    }

    for( String command : args )
    {
      if( command.startsWith("-") )
      {
        parseParameter(command, systemReader);
      }
    }

    for( String command : args )
    {
      if( !command.startsWith("-") )
      {
        executeCommand(command, systemReader);
      }
    }

    System.exit(0);
  }

  private static void parseParameter(String parameter, final SystemReader systemReader) throws Exception
  {
    switch (parameter)
    {
      case "-h":
      case "--help": {
        help();
        System.exit(0);
        break;
      }

      case "--no-git": {
        sGitCheck.set(false);
        break;
      }

      case "--skip-last": {
        sSkipLast.set(true);
        break;
      }

      default: {
        System.out.println("Unknown parameter '"+parameter+"'");
        help();
        System.exit(1);
      }
    }
  }

  private static void setupStdoutout()
  {
    final long startTime = System.currentTimeMillis();
    AtomicBoolean sharedMustWriteDate = new AtomicBoolean(true);
    System.setOut(new PrintStream(new DatedOutputStream(System.out, startTime, sharedMustWriteDate), true));
    System.setErr(new PrintStream(new DatedOutputStream(System.err, startTime, sharedMustWriteDate), true));
  }

  private static List<Zimbra> extractZimbraVersions()
  {
    String[] rawZimbraVersions = new File("zimbra/").list();
    if( rawZimbraVersions == null || rawZimbraVersions.length == 0) {
      throw new RuntimeException("Zimbra directory is empty!");
    }

    boolean hasX = false;

    List<Zimbra> zimbraVersions = new ArrayList<>(rawZimbraVersions.length);
    for( String rawZimbraVersion : rawZimbraVersions )
    {
      if( "x".equals(rawZimbraVersion) ) {
        hasX = true;
      }
      if( !Pattern.matches("[0-9.]*", rawZimbraVersion) ) {
        continue;
      }
      zimbraVersions.add( new Zimbra(Zimbra.Type.standard, new Version(rawZimbraVersion)) );
    }
    Collections.sort(zimbraVersions);

    if( zimbraVersions.isEmpty() ) {
      throw new RuntimeException("No valid zimbra version were found");
    }

    if( zimbraVersions.get(0).getVersion().compareTo(sFirstSupportedZimbraVersion) > 0 ) {
      throw new RuntimeException(
        "First zimbra version is not "+sFirstSupportedZimbraVersion+", found instead "+zimbraVersions.get(0)
      );
    }

    if( zimbraVersions.get(zimbraVersions.size()-1).getVersion().compareTo(sLastSupportedZimbraVersion) < 0 ) {
      throw new RuntimeException(
        "Last zimbra version is not "+sLastSupportedZimbraVersion+", found instead "+zimbraVersions.get(zimbraVersions.size()-1)
      );
    }

    if( hasX ) {
      zimbraVersions.add( sZimbraX );
    }

    return zimbraVersions;
  }

  private static void executeCommand(String command, final SystemReader systemReader) throws Exception {

    switch (command) {
      case "help": {
        help();
        System.exit(0);
        break;
      }

      case "setup": {
        setup(systemReader);
        return;
      }

      case "zal-dev-current-source": {
        setup(systemReader);
        buildFromSource(new Zimbra(Zimbra.Type.standard,sLastSupportedZimbraVersion), systemReader);
        return;
      }

      case "zal-dev-current-binary": {
        setup(systemReader);
        buildFromLiveZimbra(new Zimbra(Zimbra.Type.standard,sLastSupportedZimbraVersion),systemReader);
        return;
      }

      case "zal-dev-last": {
        setup(systemReader);
        buildFromZimbraVersion(new Zimbra(Zimbra.Type.standard,sLastSupportedZimbraVersion),systemReader,true);
        return;
      }

      case "zal-last": {
        setup(systemReader);
        buildFromZimbraVersion(new Zimbra(Zimbra.Type.standard,sLastSupportedZimbraVersion),systemReader,false);
        return;
      }

      case "clean": {
        removeDirectoryContent("dist/", ".*[.]jar", false);
        removeDirectoryContent("lib/", ".*[.]jar", false);
        removeDirectoryContent(
          "zimbra/",
          ".*[.](jar|xml|sql|xml-template)",
          true
        );
        return;
      }

      case "compatibility-check": {
        List<Zimbra> zimbraVersions = extractZimbraVersions();
        //check the compatibility between latest and first release
        zimbraVersions.add( zimbraVersions.get(0) );

        checkZalCompatibility(systemReader, zimbraVersions);
        return;
      }

      case "fast-compatibility-check": {
        checkZalCompatibility(systemReader, sCommonZimbraVersions);
        return;
      }

      case "zal-common": {
        setup(systemReader);
        checkOrDownloadZimbraJars();
        checkOrDownloadMavenDependencies(systemReader);
        for (final Zimbra version : sCommonZimbraVersions) {
          queueTask(new Runnable(){
            @Override
            public void run() {
              try {
                buildFromZimbraVersion(
                  version,
                  systemReader,
                  false
                );
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          });
        }
        waitTask();
        return;
      }

      case "zal-all": {
        setup(systemReader);
        checkOrDownloadZimbraJars();
        checkOrDownloadMavenDependencies(systemReader);
        for (final Zimbra version : extractZimbraVersions()) {
          queueTask(new Runnable(){
            @Override
            public void run() {
              try {
                buildFromZimbraVersion(version, systemReader, false);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          });
        }
        waitTask();
        return;
      }

      case "zal-x": {
        setup(systemReader);
        buildFromZimbraVersion(sZimbraX, systemReader, false);
        return;
      }

      default:
        break;
    }

    if( command.startsWith("zal-") ) {
      setup(systemReader);
      Version zimbraVersion = new Version(command.substring(4));
      buildFromZimbraVersion(new Zimbra(Zimbra.Type.standard, zimbraVersion), systemReader, false);
      return;
    }

    System.out.println("Unknown command '"+command+"'");
    help();
    System.exit(1);
  }

  private static void checkZalCompatibility(SystemReader systemReader, List<Zimbra> sourceList) throws Exception
  {
    AtomicBoolean failed = new AtomicBoolean(false);

    List<String> versionsPath = new LinkedList<>();
    List<String> versionsName = new LinkedList<>();

    if( systemReader.readVersion().getMicro() == 0  )
    {
      System.out.println("No need to check back-compatibility for the first micro of "+systemReader.readVersion());
    }
    else
    {
      versionsName.add( "previous version binary" );
      versionsPath.add( "bin/previous-zal-version.jar" );
    }

    sourceList.forEach(new Consumer<Zimbra>()
    {
      @Override
      public void accept(Zimbra version)
      {
        versionsPath.add("dist/"+version.toString()+"/zal.jar");
        versionsName.add(version.toString());
      }
    });

    String lastVersionName;
    String lastVersionPath;
    Iterator<String> pathIt = versionsPath.iterator();
    Iterator<String> nameIt = versionsName.iterator();
    {
      lastVersionPath = pathIt.next();
      lastVersionName = nameIt.next();
    }

    while( pathIt.hasNext() )
    {
      final String currentVersionName = nameIt.next();
      final String currentVersionPath = pathIt.next();
      final String finalLastVersionPath = lastVersionPath;
      final String finalLastVersionName = lastVersionName;
      queueTask(new Runnable() {
        @Override
        public void run() {
          try {
            System.out.println("Checking compatibility "+ finalLastVersionName +" vs "+currentVersionName+"...");
            systemReader.exec(
              "tools/japi-compliance-checker/japi-compliance-checker.pl",
              "-skip-internal-types",
              //there is no simple way to fix this incompatibility
              ".*InternalMimeHandler.*",
              "-binary",
              "-l",
              "OpenZAL",
              finalLastVersionPath,
              currentVersionPath
            );
            System.out.println("Checking compatibility "+ finalLastVersionName +" vs "+currentVersionName+"...OK");
          } catch (Exception e) {
            failed.set(false);
          }
        }
      });

      lastVersionPath = currentVersionPath;
      lastVersionName = currentVersionName;
    }
    waitTask();

    if( failed.get() ) {
      System.exit(1);
    }
  }

  private static void setup(SystemReader systemReader) throws Exception
  {
    if( !sSetupPerformed.compareAndSet(false, true) ) {
      return;
    }
    checkOrDownloadMavenDependencies(systemReader);
    checkOrDownloadZimbraJars();
  }

  private static List<Thread> sThreadList = new LinkedList<>();
  private static void queueTask(Runnable runnable) throws Exception {
    Thread thread = new Thread(runnable);
    sThreadList.add(thread);
    thread.start();

    if( sThreadList.size() >= sMaxConcurrentTask ) {
      for( Thread curr : sThreadList ) {
        curr.join();
      }
      sThreadList.clear();
    }
  }

  private static void waitTask() throws Exception
  {
    for( Thread curr : sThreadList ) {
      curr.join();
    }
    sThreadList.clear();
  }

  private static void removeDirectoryContent(String path, String regex, boolean removeDirectoryIfEmpty) {
    File dir = new File(path);

    Pattern pattern = Pattern.compile(regex);
    String[] list = dir.list();
    if( list != null ) {
      for( String name : list ) {
        File entry = new File(dir,name);
        if( entry.isDirectory() ) {
          if( entry.isHidden() ||
              Files.isSymbolicLink(FileSystems.getDefault().getPath(entry.getAbsolutePath()))) {
            System.out.println("Skipping "+entry.getPath());
            return;
          }
          removeDirectoryContent(entry.getPath(), regex, removeDirectoryIfEmpty);
          entry.delete();
        }
        else
        {
          if(pattern.matcher(name).matches()) {
            System.out.println("Removing "+entry.getPath());
            entry.delete();
          } else {
            System.out.println("Skipping "+entry.getPath());
          }
        }
      }
    }

    if( dir.list() == null || dir.list().length == 0 ) {
      System.out.println("Removing "+dir.getPath());
      dir.delete();
    }
  }

  private static void buildFromSource(Zimbra zimbra, SystemReader systemReader) throws Exception {
    checkOrDownloadMavenDependencies(systemReader);

    Build build = new Build(
      Build.JavaVersion.Java7,
      Arrays.asList("lib/", "../zm-zcs-lib/", "../zm-mailbox/" ),
      Arrays.asList("src/java/"),
      Collections.emptyList(),
      Collections.emptyList(),
      Collections.emptyList(),
      Collections.emptyList(),
      "dist/"+zimbra+"/zal.jar",
      new ZimbraVersionSourcePreprocessor( zimbra, true),
      generateManifest(zimbra, systemReader)
    );
    build.compileAll("Compiling from ../zm-zcs-lib ../zm-mailbox/ dev "+zimbra+ "...");
  }

  private static void buildFromLiveZimbra(Zimbra zimbra, SystemReader systemReader) throws Exception {
    checkOrDownloadMavenDependencies(systemReader);

    Build build = new Build(
      Build.JavaVersion.Java7,
      Arrays.asList("lib/", "/opt/zimbra/lib/jars/", "/opt/zimbra/common/jetty_home/lib/" ),
      Arrays.asList("src/java/"),
      Collections.emptyList(),
      Collections.emptyList(),
      Collections.emptyList(),
      Collections.emptyList(),
      "dist/"+zimbra+"/zal.jar",
      new ZimbraVersionSourcePreprocessor( zimbra, true),
      generateManifest(zimbra, systemReader)
    );
    build.compileAll("Compiling from /opt/zimbra dev "+zimbra+ "...");
  }

  private static void buildFromZimbraVersion(Zimbra zimbra, SystemReader systemReader, boolean devMode) throws Exception
  {
    if( sSkipLast.get() && zimbra.equals(new Zimbra(Zimbra.Type.standard,sLastSupportedZimbraVersion)) ) {
      return;
    }

    File zimbraDir = new File("zimbra/"+zimbra);
    if( !zimbraDir.exists() ) {
      throw new RuntimeException("Zimbra version "+zimbra+" not found, maybe you need to cleanup zimbra/");
    }

    Build build = new Build(
      Build.JavaVersion.Java7,
      Arrays.asList("lib/", "zimbra/"+zimbra+"/jars/"),
      Arrays.asList("src/java/"),
      Collections.emptyList(),
      Collections.emptyList(),
      Collections.emptyList(),
      Collections.emptyList(),
      (devMode ? "dist/dev-last/zal.jar" : "dist/"+zimbra+"/zal.jar"),
      new ZimbraVersionSourcePreprocessor( zimbra, devMode),
      generateManifest(zimbra, systemReader),
      new FileManager.Renamer()
      {
        @Override
        public String rename(String path)
        {
          if (sSkipDotClass.contains(path))
          {
            return path.substring(0, path.length() - ".class".length());
          }
          return path;
        }
      }
    );
    build.compileAll("Compiling ZAL "+(devMode ? "dev ":"")+zimbra+ "...");
  }

  private static void checkOrDownloadZimbraJars() throws Exception {

    boolean emptyZimbraDir = new File("zimbra/").list() == null;
    boolean brokenZimbraDir = false;

    if( !emptyZimbraDir )
    {
      try
      {
        extractZimbraVersions();
      }
      catch (RuntimeException ex) {
        System.err.println("Invalid zimbra directory: "+ex.getMessage());
        brokenZimbraDir = true;
      }
    }

    if( emptyZimbraDir || brokenZimbraDir )
    {
      FileDownloader downloader = new FileDownloader(
        "https://s3-eu-west-1.amazonaws.com/zimbra-jars/zimbra-all.tar.br"
      );
      downloader.downloadAndUnpack("zimbra/");
    }

    if( brokenZimbraDir )
    {
      try
      {
        extractZimbraVersions();
      }
      catch (RuntimeException ex) {
        System.err.println("After downloading the last zal it's still broken!");
        throw ex;
      }
    }
  }

  private static void help() {
    String format = "  %-40s%-40s\n";

    System.out.print("\n");
    System.out.printf(format,"help","Show this help message");
    System.out.printf(format,"setup", "download zal dependencies and zimbra jars");
    System.out.printf(format,"zal-all", "build zal for all zimbra versions");
    System.out.printf(format,"zal-common", "build zal for most commons zimbra versions");
    System.out.printf(format,"zal-last", "build zal against last zimbra version");
    System.out.printf(format,"zal-dev-current-source", "build zal against current zimbra source in dev mode (zimbra jar must be located in ../zm-zcs-lib and ../zm-mailbox)");
    System.out.printf(format,"zal-dev-current-binary", "build zal against current zimbra binary in dev mode (zimbra be installed in /opt/zimbra)");
    System.out.printf(format,"zal-dev-last", "build zal against last zimbra version in dev mode");
    System.out.printf(format,"zal-x", "build zal against last zimbra X version");
    System.out.printf(format,"zal-{zimbra-version}", "build zal against specified zimbra version in dev mode" );
    System.out.printf(format,"compatibility-check", "check zal Java API Compliance against all zal versions" );
    System.out.printf(format,"fast-compatibility-check", "check zal Java API Compliance only against previous zal version" );
    System.out.printf(format,"clean", "clean up temporary" );
    System.out.print("\n");
  }

  private static void checkOrDownloadMavenDependencies(SystemReader systemReader) throws Exception {
    if( !sCheckedDependencies.compareAndSet(false,true) ) {
      return;
    }

    MavenDownloader downloader = new MavenDownloader(
      "lib/",
      "com/google/code/findbugs/jsr305", "3.0.2",
      "com/fasterxml/jackson/core/jackson-core", "2.8.11",
      "com/fasterxml/jackson/core/jackson-databind", "2.8.11",
      "com/fasterxml/jackson/core/jackson-annotations", "2.8.11",
      "commons-dbutils/commons-dbutils", "1.6",
      "javax/activation/javax.activation-api", "1.2.0"
    );
    downloader.download();

    if (sGitCheck.get()) {
      TemplateWriter writer = new TemplateWriter(
        "src/java/org/openzal/zal/ZalBuildInfo.java",
        "package org.openzal.zal;\n" +
          "\n" +
          "public class ZalBuildInfo\n" +
          "{\n" +
          "    public static String COMMIT=\"${COMMIT}\";\n" +
          "    public static String VERSION=\"${VERSION}\";\n" +
          "}"
      );
      writer.add("COMMIT", systemReader.readCommit());
      writer.add("VERSION", systemReader.readVersion());
      writer.write();
      System.out.println("ZalBuildInfo generated");
    } else {
      System.out.println("Skipped ZalBuildInfo generation");
    }
  }

  private static Map<String, String> generateManifest(Zimbra zimbra, SystemReader systemReader) throws Exception {
    HashMap<String, String> manifest = new HashMap<>();
    manifest.put("Specification-Title" ,"Zimbra Abstraction Layer" );
    manifest.put("Specification-Version" ,systemReader.readVersion().toString() );
    if (sGitCheck.get()) {
      manifest.put("Specification-Commit" ,systemReader.readCommit() );
    }
    manifest.put("Specification-Vendor" ,"ZeXtras" );
    manifest.put("Implementation-Version" ,zimbra.toString() );
    manifest.put("Created-By" ,"ZeXtras" );
    manifest.put("Zimbra-Extension-Class" ,"org.openzal.zal.extension.ZalEntrypointImpl" );

    return manifest;
  }
}
