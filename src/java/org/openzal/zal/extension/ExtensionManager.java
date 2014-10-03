/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2014 ZeXtras S.r.l.
 *
 * This file is part of ZAL.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZAL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.openzal.zal.extension;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.ZalVersion;
import org.openzal.zal.lib.Version;
import org.openzal.zal.log.ZimbraLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExtensionManager
{
  @NotNull
  private final File                   mZalRoot;
  @NotNull
  private final Map<String, Extension> mExtensionMap;
  @NotNull
  private final Zimbra                 mZimbra;

  private File getCurrentJarDirectory()
  {
    return getCurrentJar().getParentFile();
  }

  private File getCurrentJar()
  {
    String classResourceName = getClass().getName().replace(".", "/") + ".class";

    URL resourceUrl = getClass().getClassLoader().getResource(classResourceName);

    if (resourceUrl == null)
    {
      throw new RuntimeException("Unable get ZAL directory");
    }

    String jarPath = resourceUrl.getPath();
    if (jarPath.contains("!"))
    {
      jarPath = jarPath.substring(0, jarPath.indexOf('!'));
      jarPath = jarPath.replace("file:", "");
    }

    return new File(jarPath);
  }

  public ExtensionManager()
  {
    mZalRoot = getCurrentJarDirectory();
    mExtensionMap = new HashMap<String, Extension>();
    mZimbra = new Zimbra();
  }

  public void loadExtensions() throws IOException
  {
    String[] entries = mZalRoot.list();

    ZimbraLog.extensions.info("Searching ZAL extensions in directory " + mZalRoot.getAbsolutePath());

    if (entries == null || entries.length == 0)
    {
      throw new IOException("Unable to read directory " + mZalRoot.getAbsolutePath());
    }
    readExtensions(mZalRoot);
  }

  public void startExtensions()
  {
    for(Extension extension : mExtensionMap.values())
    {
      ZalExtensionController controller = new ZalExtensionControllerImpl(this, extension.getExtensionClassName());
      extension.start(controller, mZimbra);
    }
  }

  public void shutdownExtension(String className)
  {
    Extension extension = mExtensionMap.remove(className);
    extension.shutdown();
  }

  public void rebootExtension(String className)
  {
    Extension extension = mExtensionMap.remove(className);
    extension.shutdown();

    //Extension newExtension =
  }

  public void reloadExtension(String className, File directory)
  {

  }

  public void updateExtension(String className, File directory)
  {

  }

  public void stopExtensions()
  {
    for(Extension extension : mExtensionMap.values())
    {
      extension.shutdown();
    }
  }

  private class ExtensionInfo
  {
    public String getExtensionClass()
    {
      return mExtensionClass;
    }

    public Version getRequiredVersion()
    {
      return mRequiredVersion;
    }

    public String getZalExtensionName()
    {
      return mZalExtensionName;
    }

    private final String mExtensionClass;
    private final String  mZalExtensionName;
    private final Version mRequiredVersion;

    public ExtensionInfo(String extensionClass, String zalExtensionName, Version requiredVersion)
    {
      mExtensionClass = extensionClass;
      mZalExtensionName = zalExtensionName;
      mRequiredVersion = requiredVersion;
    }

    public boolean isCompatible(Version currentVersion)
    {
      return currentVersion.truncate(2).equals(mRequiredVersion.truncate(2)) &&
             currentVersion.isAtLeast(mRequiredVersion);
    }
  }

  private void readExtensions(File parentDirectory)
  {
    String files[] = parentDirectory.list();

    if (files == null || files.length == 0) {
      return;
    }

    LinkedList<File> libraries = new LinkedList<File>();
    LinkedList<ExtensionInfo> extensionInfoList = new LinkedList<ExtensionInfo>();

    for (String fileName : files)
    {
      File file = new File(parentDirectory, fileName);
      if (!file.isFile()) {
        continue;
      }

      try
      {
        ZipFile zipFile = new ZipFile(file);

        libraries.add(file);

        ZipEntry entry = zipFile.getEntry("META-INF/MANIFEST.MF");
        if (entry == null) {
          continue;
        }

        InputStream stream = null;
        try
        {
          stream = zipFile.getInputStream(entry);
          Manifest manifest = new Manifest(stream);

          String zalExtensionClass = manifest.getMainAttributes().getValue("ZAL-Extension-Class");
          if (zalExtensionClass == null || zalExtensionClass.isEmpty())
          {
            continue;
          }

          String zalExtensionName = manifest.getMainAttributes().getValue("ZAL-Extension-Name");
          if (zalExtensionName == null || zalExtensionName.isEmpty())
          {
            zalExtensionName = zalExtensionClass;
          }


          String zalRequiredVersion = manifest.getMainAttributes().getValue("ZAL-Required-Version");
          if (zalRequiredVersion == null || zalRequiredVersion.isEmpty())
          {
            zalRequiredVersion = ZalVersion.current.toString();
          }

          ExtensionInfo extensionInfo = new ExtensionInfo(
            zalExtensionClass,
            zalExtensionName,
            new Version(zalRequiredVersion)
          );
          extensionInfoList.add(extensionInfo);
        }
        finally
        {
          if (stream != null)
          {
            stream.close();
          }
        }
      }
      catch (IOException e)
      {
        ZimbraLog.extensions.warn("Ignoring file non zip-file " + file.getAbsolutePath());
        continue;
      }

      if (extensionInfoList.isEmpty())
      {
        ZimbraLog.extensions.warn("No extension loaded from directory " + parentDirectory.getAbsolutePath());
        return;
      }
    }

    for( ExtensionInfo info : extensionInfoList )
    {
      if( !info.isCompatible(ZalVersion.current) )
      {
        ZimbraLog.extensions.warn(
          "Unable to load extension "+info.getZalExtensionName()+": it requires ZAL version "+info.getRequiredVersion().toString()+" but current version is "+ZalVersion.current.toString()
        );
        continue;
      }

      try
      {
        Extension extension = new Extension(
          info.getExtensionClass(),
          libraries
        );

        mExtensionMap.put(
          info.getExtensionClass(),
          extension
        );
      }
      catch (ClassNotFoundException e)
      {
        ZimbraLog.extensions.warn("Unable to create extension "+info.getZalExtensionName(), e);
      }
    }
  }
}
