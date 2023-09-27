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

package org.openzal.zal.tools;

import javax.annotation.Nullable;
import org.openzal.zal.extension.BootstrapClassLoader;
import org.openzal.zal.lib.Version;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class VersionChooser
{
  public File getBestVersionDirectory(File extensionPathFile) throws IOException
  {
    File extensionRootDirectory = new File(readPath(extensionPathFile));
    Version bestVersion = getBestVersion(extensionRootDirectory);

    if (bestVersion == null)
    {
      throw new RuntimeException("cannot find a valid extension x.y.z in directory: " + extensionRootDirectory.getAbsolutePath());
    }

    return new File(extensionRootDirectory, bestVersion.toString());
  }

  private String readPath(File extensionPathFile) throws IOException
  {
    StringBuilder stringBuffer = new StringBuilder(1024);

    InputStream in = new BufferedInputStream(new FileInputStream(extensionPathFile));
    try
    {
      while(true)
      {
        int read = in.read();
        if( read == -1 )
        {
          break;
        }

        stringBuffer.append((char)read);
      }
    }
    finally
    {
      in.close();
    }

    return stringBuffer.toString().replaceAll("[\r\n]*", "");
  }

  @Nullable
  private Version getBestVersion(File realExtensionDirectory)
  {
    Version bestVersion = null;
    File[] files = realExtensionDirectory.listFiles();

    if( files != null )
    {
      for( File node : files )
      {
        if( node.isDirectory() )
        {
          String name = node.getName();

          if( name.matches("[0-9]+\\.[0-9]+\\.[0-9]+") )
          {
            Version currentVersion = Version.parse(name);
            if( bestVersion == null || bestVersion.lessThan(currentVersion) )
            {
              bestVersion = currentVersion;
            }
          }
        }
      }
    }
    return bestVersion;
  }

  private BootstrapClassLoader createClassLoader(List<File> fileList)
  {
    List<URL> urls = new ArrayList<URL>(fileList.size());

    for (File file : fileList)
    {
      try
      {
        urls.add(new URL("file:" + file.getAbsolutePath()));
      }
      catch (MalformedURLException e)
      {
        throw new RuntimeException(e);
      }
    }

    return new BootstrapClassLoader(
      urls.toArray(new URL[urls.size()]),
      this.getClass().getClassLoader(),
      false
    );
  }

  public BootstrapClassLoader getBootstrapClassLoader(File chosenExtensionDirectory)
  {
    File[] nodes = chosenExtensionDirectory.listFiles();
    List<File> fileList = new LinkedList<File>();
    if (nodes != null)
    {
      for (File node : nodes)
      {
        if (node.isFile())
        {
          fileList.add(node);
        }
      }
    }

    return createClassLoader(fileList);
  }
}
