import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class ZalBuilder
{
  private static final int     sMaxConcurrentTask = 4;
  private static final Version sFirstSupportedZimbraVersion = new Version("8.0.0");
  private static final Version sLastSupportedZimbraVersion = new Version("8.8.11");

  private static AtomicBoolean sCheckedDependencies = new AtomicBoolean(false);
  private static String[] sCommonZimbraVersions = {
    "8.0.0", "8.0.9", "8.6.0", "8.7.11", "8.8.10", "8.8.11"
  };

  public static void main(String args[])
    throws Exception
  {
    SystemReader systemReader = new SystemReader();

    System.out.println("  ZAL - Version "+systemReader.readVersion());
    if( args.length == 0 ) {
      help();
      System.exit(1);
    }
    for( String command : args ) {
      executeCommand(command, systemReader);
    }

    System.exit(0);
  }

  private static List<Version> extractZimbraVersions()
  {
    String[] rawZimbraVersions = new File("zimbra/").list();
    if( rawZimbraVersions == null || rawZimbraVersions.length == 0) {
      System.out.println("zimbra/ is empty!");
      System.exit(1);
      return null;
    }

    List<Version> zimbraVersions = new ArrayList<>(rawZimbraVersions.length);
    for( String rawZimbraVersion : rawZimbraVersions ) {
      zimbraVersions.add( new Version(rawZimbraVersion) );
    }
    Collections.sort(zimbraVersions);

    if( zimbraVersions.get(0).compareTo(sFirstSupportedZimbraVersion) > 0 ) {
      throw new RuntimeException(
        "First zimbra version is not "+sFirstSupportedZimbraVersion+", found instead "+zimbraVersions.get(0)
      );
    }

    if( zimbraVersions.get(zimbraVersions.size()-1).compareTo(sLastSupportedZimbraVersion) < 0 ) {
      throw new RuntimeException(
        "Last zimbra version is not "+sLastSupportedZimbraVersion+", found instead "+zimbraVersions.get(zimbraVersions.size()-1)
      );
    }

    return zimbraVersions;
  }

  private static void executeCommand(String command, final SystemReader systemReader) throws Exception {

    switch (command) {
      case "--help":
      case "-h":
      case "help": {
        help();
        System.exit(0);
        break;
      }

      case "zal-dev-current-source": {
        buildFromSource(sLastSupportedZimbraVersion,systemReader);
        return;
      }

      case "zal-dev-current-binary": {
        buildFromLiveZimbra(sLastSupportedZimbraVersion,systemReader);
        return;
      }

      case "zal-dev-last": {
        buildFromZimbraVersion(sLastSupportedZimbraVersion,systemReader,true);
        return;
      }

      case "clean": {
        removeDirectoryContent("dist/", ".*[.]jar", false);
/*
        Uncomment when ant-based build system is removed
        removeDirectoryContent("lib/", ".*[.]jar");
*/
        removeDirectoryContent(
          "zimbra/",
          ".*[.](jar|xml|sql|xml-template)",
          true
        );
        return;
      }

      case "compatibility-check": {
        if( systemReader.readVersion().getMicro() == 0  ) {
          System.out.println("No need to check compatibility for the first micro of "+systemReader.readVersion());
          return;
        }

        List<Version> zimbraVersions = extractZimbraVersions();
        //check the compatibility between latest and first release
        zimbraVersions.add( zimbraVersions.get(0) );

        String lastVersion = "previous version binary";
        String lastVersionPath = "bin/previous-zal-version.jar";
        for( Version zimbraVersion :zimbraVersions) {
          System.out.println("Checking compatibility "+lastVersion+" vs "+zimbraVersion);
          final String currentVersion = "dist/"+zimbraVersion+"/zal.jar";
          final String finalLastVersionPath = lastVersionPath;
          queueTask(new Runnable() {
            @Override
            public void run() {
              try {
                systemReader.exec(
                  "tools/japi-compliance-checker/japi-compliance-checker.pl",
                  "-binary",
                  "-l",
                  "OpenZAL",
                  finalLastVersionPath,
                  currentVersion
                );
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          });

          lastVersionPath = currentVersion;
          lastVersion = zimbraVersion.toString();
        }
        waitTask();
        return;
      }

      case "fast-compatibility-check": {
        if( systemReader.readVersion().getMicro() == 0  ) {
          System.out.println("No need to check compatibility for the first micro of "+systemReader.readVersion());
          return;
        }
        String lastVersion = "bin/previous-zal-version.jar";
        for( String zimbraVersion : sCommonZimbraVersions) {
          final String currentVersion = "dist/"+zimbraVersion+"/zal.jar";
          final String finalLastVersion = lastVersion;
          queueTask(new Runnable() {
            @Override
            public void run() {
              try {
                systemReader.exec(
                  "tools/japi-compliance-checker.pl",
                  "-binary",
                  "-l",
                  "OpenZAL",
                  finalLastVersion,
                  currentVersion
                );
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          });

          lastVersion = currentVersion;
        }
        waitTask();
        return;
      }

      case "zal-common": {
        checkOrDownloadZimbraJars();
        checkOrDownloadMavenDependencies(systemReader);
        for (final String rawVersion : sCommonZimbraVersions) {
          queueTask(new Runnable(){
            @Override
            public void run() {
              try {
                buildFromZimbraVersion(new Version(rawVersion), systemReader, false);
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
        checkOrDownloadZimbraJars();
        checkOrDownloadMavenDependencies(systemReader);
        for (final Version version : extractZimbraVersions()) {
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

      default:
        break;
    }

    if( command.startsWith("zal-") ) {
      Version zimbraVersion = new Version(command.substring(4));
      buildFromZimbraVersion(zimbraVersion, systemReader, false);
      return;
    }

    System.out.println("Unknown command '"+command+"'");
    help();
    System.exit(1);
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

  private static void buildFromSource(Version version, SystemReader systemReader) throws Exception {
    checkOrDownloadMavenDependencies(systemReader);

    Build build = new Build(
      Arrays.asList("lib/", "../zm-zcs-lib/", "../zm-mailbox/" ),
      Arrays.asList("src/java/"),
      "dist/"+version+"/zal.jar",
      new ZimbraVersionSourcePreprocessor( version, true),
      generateManifest(version, systemReader)
    );
    build.compileAll("Compiling from ../zm-zcs-lib ../zm-mailbox/ dev "+version+ "...");
  }

  private static void buildFromLiveZimbra(Version version, SystemReader systemReader) throws Exception {
    checkOrDownloadMavenDependencies(systemReader);

    Build build = new Build(
      Arrays.asList("lib/", "/opt/zimbra/lib/jars/", "/opt/zimbra/common/jetty_home/lib/" ),
      Arrays.asList("src/java/"),
      "dist/"+version+"/zal.jar",
      new ZimbraVersionSourcePreprocessor( version, true),
      generateManifest(version, systemReader)
    );
    build.compileAll("Compiling from /opt/zimbra dev "+version+ "...");
  }

  private static void buildFromZimbraVersion(Version version, SystemReader systemReader, boolean devMode) throws Exception {
    checkOrDownloadMavenDependencies(systemReader);
    checkOrDownloadZimbraJars();

    File zimbraDir = new File("zimbra/"+version);
    if( !zimbraDir.exists() ) {
      throw new RuntimeException("Zimbra version "+version+" not found, maybe you need to cleanup zimbra/");
    }

    Build build = new Build(
      Arrays.asList("lib/", "zimbra/"+version+"/jars/"),
      Arrays.asList("src/java/"),
      (devMode ? "dist/dev-last/zal.jar" : "dist/"+version+"/zal.jar"),
      new ZimbraVersionSourcePreprocessor( version, devMode),
      generateManifest(version, systemReader)
    );
    build.compileAll("Compiling "+(devMode ? "dev ":"")+version+ "...");
  }

  private static void checkOrDownloadZimbraJars() throws Exception {
    if( new File("zimbra/").list() == null )
    {
      FileDownloader downloader = new FileDownloader(
        "https://s3-eu-west-1.amazonaws.com/zimbra-jars/zimbra-all.tar.br"
      );
      downloader.downloadAndUnpack("zimbra/");
    }
  }

  private static void help() {
    String format = "  %-40s%-40s\n";

    System.out.print("\n");
    System.out.printf(format,"help","Show this help message");
    System.out.printf(format,"zal-all", "build zal for all zimbra versions");
    System.out.printf(format,"zal-common", "build zal for most commons zimbra versions");
    System.out.printf(format,"zal-dev-current-source", "build zal against current zimbra source in dev mode (zimbra jar must be located in ../zm-zcs-lib and ../zm-mailbox)");
    System.out.printf(format,"zal-dev-current-binary", "build zal against current zimbra binary in dev mode (zimbra be installed in /opt/zimbra)");
    System.out.printf(format,"zal-dev-last", "build zal against last zimbra version in dev mode");
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
      "org/jetbrains/annotations", "16.0.3",
      "com/fasterxml/jackson/core/jackson-core", "2.6.7",
      "com/fasterxml/jackson/core/jackson-databind", "2.6.7",
      "com/fasterxml/jackson/core/jackson-annotations", "2.6.7",
      "commons-dbutils/commons-dbutils", "1.6"
    );
    downloader.download();

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
  }

  private static Map<String, String> generateManifest(Version zimbraVersion, SystemReader systemReader) throws Exception {
    HashMap<String, String> manifest = new HashMap<>();
    manifest.put("Specification-Title" ,"Zimbra Abstraction Layer" );
    manifest.put("Specification-Version" ,systemReader.readVersion().toString() );
    manifest.put("Specification-Commit" ,systemReader.readCommit() );
    manifest.put("Specification-Vendor" ,"ZeXtras" );
    manifest.put("Implementation-Version" ,zimbraVersion.toString() );
    manifest.put("Created-By" ,"ZeXtras" );
    manifest.put("Zimbra-Extension-Class" ,"org.openzal.zal.extension.ZalEntrypointImpl" );

    return manifest;
  }
}
