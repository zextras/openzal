import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/*
  J2SE 8    = 52.0
  J2SE 7    = 51.0
  J2SE 6.0  = 50.0
  J2SE 5.0  = 49.0
  JDK  1.4  = 48.0
  JDK  1.3  = 47.0
  JDK  1.2  = 46.0
  JDK  1.1  = 45.0
*/

public class ClassVersionChecker
{
  public static void main(String[] args) throws IOException
  {
    if( args.length <= 2 )
    {
      throw new RuntimeException("Syntax: <max JDK version> jar|path [jar|path] ...");
    }

    int maxVersion = Integer.valueOf(args[0]);
    System.out.println("Max class version: "+maxVersion );

    for (int i = 1; i < args.length; i++)
    {
      File entry = new File(args[i]);
      if( entry.isDirectory() )
      {
        checkDir(maxVersion, entry);
      }
      else
      {
        checkJar(maxVersion, entry);
      }
    }
  }

  public static void checkDir(int maxVersion, File entry) throws IOException
  {
    File[] subFiles = entry.listFiles();

    if( subFiles != null )
    {
      for (File file : subFiles)
      {
        if( file.isDirectory() )
        {
          checkDir(maxVersion, file);
        }
        else
        {
          if( file.getName().endsWith(".jar") )
          {
            checkJar(maxVersion, file);
          }
        }
      }
    }
  }

  public static void checkJar(int maxVersion, File filename)
    throws IOException
  {
    JarFile jarFile = new JarFile(filename);
    Enumeration<JarEntry> it = jarFile.entries();
    while (it.hasMoreElements())
    {
      JarEntry jarEntry = it.nextElement();

      String name = jarEntry.getName();
      if ( !name.startsWith("META-INF") && name.endsWith(".class"))
      {
        checkInputStream( jarEntry.getName(), maxVersion, jarFile.getInputStream(jarEntry) );
      }
    }
  }

  private static void checkInputStream(String name, int maxVersion, InputStream inputStream) throws IOException
  {
    DataInputStream in = new DataInputStream(inputStream);
    try
    {
      int magic = in.readInt();
      if (magic != 0xCAFEBABE)
      {
        throw new RuntimeException("Invalid magic number for class "+name);
      }

      int minor = in.readUnsignedShort();
      int major = in.readUnsignedShort();

      if (major > maxVersion)
      {
        throw new RuntimeException("Invalid version "+major+"."+minor+" for class "+name);
      }
    }
    finally
    {
      in.close();
    }
  }
}
