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
import java.util.Arrays;

public class ConsoleBoot
{
  private static final VersionChooser sVersionChooser = new VersionChooser();

  public static void main(String[] args) throws Exception
  {
    if( args.length == 0 )
    {
      throw new RuntimeException("Missing CLI class");
    }

    String cliClassName = args[0];

    File directory = JarUtils.getCurrentJar().getParentFile();
    File extensionPathFile = new File(directory, "extension-path");
    BootCli bootCli;
    if (extensionPathFile.exists())
    {
      File path = sVersionChooser.getBestVersionDirectory(extensionPathFile);
      bootCli = createBootCli(path, cliClassName);
    }
    else
    {
      bootCli = createBootCli(directory, cliClassName);
    }

    bootCli.run(Arrays.copyOfRange(args,1,args.length));
  }

  private static BootCli createBootCli(File extensionDirectory, String cliClassName) throws IOException
  {
    return new BootCli(sVersionChooser.getBootstrapClassLoader(extensionDirectory), cliClassName);
  }
}
