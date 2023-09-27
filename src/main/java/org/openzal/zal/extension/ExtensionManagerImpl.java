/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2016 ZeXtras S.r.l.
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

import org.apache.commons.io.IOUtils;
import org.openzal.zal.ZalVersion;
import org.openzal.zal.lib.Version;
import org.openzal.zal.log.ZimbraLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.LinkedList;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class ExtensionManagerImpl implements ExtensionManager
{
  private       File                   mExtensionDirectory;
  private       Extension              mExtension;
  private       ZalExtensionController mCustomZalExtensionController;
  private       ClassLoader            mCustomClassLoader;

  public Extension getExtension()
  {
    return mExtension;
  }

  public ExtensionManagerImpl()
  {
    mCustomClassLoader = null;
    mCustomZalExtensionController = null;
    mExtension = null;
    mExtensionDirectory = getCurrentJarDirectory();
  }

  @Override
  public void setCustomClassLoader(ClassLoader classLoader)
  {
    mCustomClassLoader = classLoader;
  }

  @Override
  public void setCustomZalExtensionController(ZalExtensionController customZalExtensionController)
  {
    mCustomZalExtensionController = customZalExtensionController;
  }

  @Override
  public void setCustomExtensionDirectory(File extensionDirectory)
  {
    mExtensionDirectory = extensionDirectory;
  }

  private ZalExtensionController getController()
  {
    if (mCustomZalExtensionController != null)
    {
      return mCustomZalExtensionController;
    }
    else
    {
      return new StubZalExtensionController();
    }
  }

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

  @Override
  public void loadExtension() throws IOException
  {
    String[] entries = mExtensionDirectory.list();

    ZimbraLog.extensions.info("Searching ZAL extension in directory " + mExtensionDirectory.getAbsolutePath());

    if (entries == null || entries.length == 0)
    {
      throw new IOException("Unable to read directory " + mExtensionDirectory.getAbsolutePath());
    }
    readExtension(mExtensionDirectory);
  }

  @Override
  public void startExtension(WeakReference<ClassLoader> previousExtension)
  {
    if( mExtension != null)
    {
      mExtension.start(getController(), previousExtension);
    }
  }

  @Override
  public void shutdownExtension()
  {
    if( mExtension != null)
    {
      mExtension.shutdown();
    }
  }

  private static class ExtensionInfo
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
      return withoutPatch(currentVersion).equals(withoutPatch(mRequiredVersion)) &&
             currentVersion.isAtLeast(mRequiredVersion);
    }

    static Version withoutPatch(Version v) {
      return Version.of(v.getMajor(), v.getMinor(), 0);
    }
  }

  private void readExtension(File parentDirectory)
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

      ZipFile zipFile = null;
      try
      {
        zipFile = new ZipFile(file);

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
            Version.parse(zalRequiredVersion)
          );
          extensionInfoList.add(extensionInfo);
        }
        finally
        {
          IOUtils.closeQuietly(stream);
          if( zipFile != null )
          {
            zipFile.close();
          }
        }
      }
      catch (IOException e)
      {
        ZimbraLog.extensions.warn("Ignoring file non zip-file " + file.getAbsolutePath());
        continue;
      }
    }

    if (extensionInfoList.isEmpty())
    {
      ZimbraLog.extensions.fatal("No extension loaded from directory " + parentDirectory.getAbsolutePath());
      throw new RuntimeException("No extension loaded from directory " + parentDirectory.getAbsolutePath());
    }

    for( ExtensionInfo info : extensionInfoList )
    {
      try
      {
        Extension extension;
        if( mCustomClassLoader != null )
        {
          extension = new Extension(
            info.getExtensionClass(),
            mCustomClassLoader
          );
        }
        else
        {
          extension = new Extension(
            info.getExtensionClass(),
            libraries
          );
        }

        mExtension = extension;
        break;
      }
      catch (ClassNotFoundException e)
      {
        ZimbraLog.extensions.warn("Unable to create extension "+info.getZalExtensionName(), e);
      }
    }
  }
}
