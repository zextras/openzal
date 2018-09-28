import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ZalBuilder
{
  public static void main(String args[])
    throws Exception
  {
    if( new File("zimbra/").list() == null )
    {
      FileDownloader downloader = new FileDownloader(
        "https://s3-eu-west-1.amazonaws.com/zimbra-jars/zimbra-all.tar.xz",
        "tmp/zimbra-all.tar.xz"
      );
      downloader.download();
      downloader.unpack("zimbra/");
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

    String[] rawVersions;
    if( args.length == 0 )
    {
      rawVersions = new File("zimbra/").list();
      if( rawVersions == null ) {
        throw new RuntimeException("Directory zimbra/ is empty, maybe you need to extract zimbra-all.tar.xz");
      }
    }
    else
    {
      rawVersions = args;
    }

    List<Version> zimbraVersions = new ArrayList<>(
      rawVersions.length
    );
    for( String strVersion : rawVersions ) {
      zimbraVersions.add( new Version(strVersion) );

    }
    Collections.sort(zimbraVersions);

    for( Version version : zimbraVersions ) {
      System.out.println("Compiling "+version+ "...");
      File zimbraDir = new File("zimbra/"+version);
      if( !zimbraDir.exists() ) {
        throw new RuntimeException("Zimbra version "+version+" not found");
      }

      Build build = new Build(
        Arrays.asList("lib/", "zimbra/"+version+"/jars/"),
        "dist/"+version+"/zal.jar",
        new ZimbraVersionSourcePreprocessor( version, false)
      );
      build.compileAll();
    }
  }
}
