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

import org.openzal.zal.lib.Version;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TinyBoot
{
  private final File mExtensionPathFile;
  private final static String sZalEntrypointName = "org.openzal.zal.extension.ZalEntrypointImpl";

  public TinyBoot(File extensionPathFile)
  {
    mExtensionPathFile = extensionPathFile;
  }

  public ZalEntrypoint createZalEntryPoint(File extensionDirectory, ZalExtensionController zalExtensionController) throws Exception
  {
    BootstrapClassLoader bootstrapClassLoader = getBootstrapClassLoader(extensionDirectory);

    Class<ZalEntrypoint> zalEntrypoint = (Class<ZalEntrypoint>) bootstrapClassLoader.loadClass(sZalEntrypointName);

    ZalEntrypoint entrypoint = zalEntrypoint.newInstance();
    entrypoint.provideCustomClassLoader(bootstrapClassLoader);
    entrypoint.provideCustomExtensionController(zalExtensionController);
    entrypoint.provideCustomExtensionDirectory(extensionDirectory);

    return entrypoint;
  }

  public ZalEntrypoint createZalEntryPoint(ZalExtensionController controller) throws Exception
  {
    File extensionRootDirectory = new File(readPath());
    Version bestVersion = getBestVersion(extensionRootDirectory);

    if (bestVersion == null)
    {
      throw new RuntimeException("cannot find a valid extension x.y.z in directory: " + extensionRootDirectory.getAbsolutePath());
    }

    File chosenExtensionDirectory = new File(extensionRootDirectory, bestVersion.toString());

    return createZalEntryPoint(chosenExtensionDirectory, controller);
  }

  private BootstrapClassLoader getBootstrapClassLoader(File chosenExtensionDirectory)
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

  private BootstrapClassLoader createClassLoader(List<File> fileList)
  {
    List<URL> urls = new ArrayList<URL>(fileList.size());

    for (File file : fileList)
    {
      try
      {
        urls.add(new URL("jar:file:" + file.getAbsolutePath() + "!/"));
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
            Version currentVersion = new Version(name);
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

  private String readPath() throws IOException
  {
    StringBuilder stringBuffer = new StringBuilder(1024);

    InputStream in = new BufferedInputStream(new FileInputStream(mExtensionPathFile));
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
}
