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

package org.openzal.zal.tools;

import java.util.jar.Manifest;
import java.util.zip.ZipFile;

public class ChecksumChecker
{
  public static void main(String ags[]) throws Exception
  {
    ZipFile zipFile = new ZipFile(JarUtils.getCurrentJar());

    Manifest manifest = JarUtils.getManifest(zipFile);
    String writtenDigest = manifest.getMainAttributes().getValue("Digest");
    String currentDigest = JarUtils.computeDigest(zipFile);

    if( currentDigest.equals(writtenDigest) )
    {
      System.out.println("OK");
      System.exit(0);
    }
    else
    {
      System.out.println("FAIL");
      System.exit(10);
    }
  }
}
