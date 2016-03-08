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

import org.openzal.zal.lib.JarAccessor;

import java.io.File;
import java.io.IOException;

public class ConsoleBoot
{
  private static final VersionChooser S_VERSION_CHOOSER       = new VersionChooser();
  private static final String         EXTENSION_CLI_ATTRIBUTE = "ZAL-ExtensionCli-Class";

  public static void main(String[] args) throws Exception
  {
    File directory = JarUtils.getCurrentJar().getParentFile();
    File extensionPathFile = new File(directory, "extension-path");
    BootCli bootCli;
    if (extensionPathFile.exists())
    {
      File path = S_VERSION_CHOOSER.getBestVersionDirectory(extensionPathFile);
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
    return new BootCli(S_VERSION_CHOOSER.getBootstrapClassLoader(extensionDirectory), getExtensionCli(extensionDirectory));
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
