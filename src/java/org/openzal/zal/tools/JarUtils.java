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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public abstract class JarUtils
{
  private static final String MANIFEST = "META-INF/MANIFEST.MF";

  @NotNull
  public static File getJar(Class cls)
  {
    String classResourceName = cls.getName().replace(".", "/") + ".class";

    URL resourceUrl = cls.getClassLoader().getResource(classResourceName);

    if (resourceUrl == null)
    {
      throw new RuntimeException("Unable get jar path");
    }

    String jarPath = resourceUrl.getPath();
    if( jarPath.contains("!") ) {
      jarPath = jarPath.substring(0, jarPath.indexOf('!'));
      jarPath = jarPath.replace("file:","");
    }

    return new File(jarPath);
  }

  @NotNull
  public static File getCurrentJar()
  {
    return getJar(JarUtils.class);
  }

  public static void copyJar(ZipFile zipFile, Manifest manifest, File destination) throws IOException
  {
    ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(destination));

    Enumeration<? extends ZipEntry> it = zipFile.entries();
    while( it.hasMoreElements() )
    {
      ZipEntry zipEntry = it.nextElement();

      if( zipEntry.getName().equals(MANIFEST) )
      {
        zipOutputStream.putNextEntry(new ZipEntry(MANIFEST));
        manifest.write(zipOutputStream);
      }
      else
      {
        zipOutputStream.putNextEntry(zipEntry);
        zipOutputStream.write(
          readFile(
            (int)zipEntry.getSize(),
            zipFile.getInputStream(zipEntry)
          )
        );
      }
    }

    zipOutputStream.close();
  }

  private static class NameComparator implements Comparator<ZipEntry>
  {
    @Override
    public int compare(ZipEntry o1, ZipEntry o2)
    {
      return o1.getName().compareTo(o2.getName());
    }
  }

  public static byte[] readFile(int size, InputStream inputStream) throws IOException
  {
    byte[] buffer = new byte[size];
    int read = 0;

    while( true )
    {
      int currentRead = inputStream.read(buffer, read, size-read);
      if( currentRead < 0 ){
        break;
      }
      read += currentRead;
    }

    inputStream.close();

    if( read != size ) {
      throw new IOException("invalid read size");
    }

    return buffer;
  }

  public static String printableDigest(MessageDigest digest)
  {
    byte [] buffer = digest.digest();
    StringBuilder sb = new StringBuilder(buffer.length*2);

    for( int i=0; i < buffer.length; ++i )
    {
      if( (buffer[i] & 0xFF) < 0x10 ) sb.append('0');
      sb.append(Integer.toHexString(buffer[i] & 0xFF));
    }

    return sb.toString();
  }

  public static Manifest getManifest(ZipFile zipFile) throws IOException
  {
    ZipEntry manifestEntry = zipFile.getEntry(MANIFEST);
    return new Manifest(zipFile.getInputStream(manifestEntry));
  }

  public static String computeDigest(ZipFile zipFile) throws NoSuchAlgorithmException, IOException
  {
    LinkedList<ZipEntry> zipEntries = new LinkedList<ZipEntry>();

    Enumeration<? extends ZipEntry> it = zipFile.entries();
    while( it.hasMoreElements() )
    {
      ZipEntry zipEntry = it.nextElement();
      if( zipEntry.getName().equals("META-INF/MANIFEST.MF") ) {
        continue;
      }
      zipEntries.add(zipEntry);
    }

    Collections.sort(zipEntries, new NameComparator());

    MessageDigest digest = MessageDigest.getInstance("SHA1");
    for( ZipEntry entry : zipEntries )
    {
      digest.update(
        entry.getName().getBytes("UTF-8")
      );
      digest.update(
        readFile((int)entry.getSize(), zipFile.getInputStream(entry))
      );
    }

    return printableDigest(digest);
  }
}
