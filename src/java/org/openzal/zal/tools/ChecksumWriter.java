/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2015 ZeXtras S.r.l.
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.jar.Manifest;
import java.util.zip.ZipFile;

public class ChecksumWriter
{
  public static void main(@NotNull String args[]) throws Exception
  {
    if( args.length == 0 )
    {
      System.err.println("No destination file provided");
      System.exit(1);
    }

    File destination = new File(args[0]);
    ZipFile zipFile = new ZipFile(JarUtils.getCurrentJar());
    String currentDigest = JarUtils.computeDigest(zipFile);

    Manifest manifest = JarUtils.getManifest(zipFile);
    manifest.getMainAttributes().putValue("Digest", currentDigest);

    JarUtils.copyJar(zipFile, manifest, destination);
    System.exit(0);
  }
}
