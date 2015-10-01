package org.openzal.zal.tools;

import org.openzal.zal.lib.JarAccessor;

import java.io.File;
import java.io.IOException;

public class ConsoleBoot
{
  private static final ExtensionLoader sExtensionLoader = new ExtensionLoader();
  private static final String          EXTENSION_CLI_ATTRIBUTE = "ZAL-ExtensionCli-Class";

  public static void main(String[] args) throws Exception
  {
    File directory = JarUtils.getCurrentJar().getParentFile();
    File extensionPathFile = new File(directory, "extension-path");
    BootCli bootCli;
    if (extensionPathFile.exists())
    {
      File path = sExtensionLoader.getBestVersionDirectory(extensionPathFile);
      bootCli = createBootCli(path);
    }
    else
    {
      bootCli = createBootCli(directory);
    }

    bootCli.run(args);
  }

  private static BootCli createBootCli(File extensionDirectory) throws IOException
  {
    return new BootCli(sExtensionLoader.getBootstrapClassLoader(extensionDirectory), getExtensionCli(extensionDirectory));
  }

  private static String getExtensionCli(File extensionDirectory) throws IOException
  {
    File[] nodes = extensionDirectory.listFiles();
    if (nodes != null)
    {
      for (File jar : nodes)
      {
        JarAccessor jarAccessor = new JarAccessor(jar);

        String extensionClass = jarAccessor.getAttributeInManifest(EXTENSION_CLI_ATTRIBUTE);
        if (extensionClass != null && !extensionClass.isEmpty())
        {
          return extensionClass;
        }
      }
    }

    throw new RuntimeException("No CLI found");
  }

}
