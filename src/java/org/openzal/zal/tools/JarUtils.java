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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public abstract class JarUtils
{
  private static final String MANIFEST = "META-INF/MANIFEST.MF";
  private static final String DIGEST = "DIGEST";
  private static final String SIGNATURE = "SIGNATURE";
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  public static File getJarPathOfClass(Class cls)
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

  public static File getCurrentJar()
  {
    return getJarPathOfClass(JarUtils.class);
  }

  public static void copyJar(ZipFile zipFile, File destination, byte[] digest, byte[] signature) throws IOException
  {
    ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(destination));

    byte[] buffer = new byte[32*1024];
    Enumeration<? extends ZipEntry> it = zipFile.entries();
    while( it.hasMoreElements() )
    {
      ZipEntry zipEntry = (ZipEntry) it.nextElement().clone();
      zipEntry.setCompressedSize(-1);
      InputStream inputStream = zipFile.getInputStream(zipEntry);
      try
      {
        zipOutputStream.putNextEntry(zipEntry);
        copyStream(buffer, inputStream, zipOutputStream);
      }
      finally
      {
        inputStream.close();
      }
    }

    zipOutputStream.putNextEntry(new ZipEntry(DIGEST));
    zipOutputStream.write(printableByteArray(digest).getBytes("UTF-8"));

    zipOutputStream.putNextEntry(new ZipEntry(SIGNATURE));
    zipOutputStream.write(printableByteArray(signature).getBytes("UTF-8"));

    zipOutputStream.close();
  }

  private static void copyStream(byte[] buffer, InputStream inputStream, ZipOutputStream zipOutputStream) throws IOException
  {
    int read;
    while ( (read = inputStream.read(buffer, 0, buffer.length) ) > -1)
    {
      zipOutputStream.write(buffer, 0, read);
    }
  }

  public static String printableByteArray(byte[] buffer)
  {
    char[] encoded = new char[2 * buffer.length];
    for( int i=0; i < buffer.length; ++i )
    {
      int value = buffer[i] & 0xFF;
      encoded[i*2] = HEX_ARRAY[value >>> 4];
      encoded[i*2 + 1] = HEX_ARRAY[value & 0x0F];
    }

    return new String(encoded);
  }

  public static Manifest getManifest(ZipFile zipFile) throws IOException
  {
    ZipEntry manifestEntry = zipFile.getEntry(MANIFEST);
    if (manifestEntry == null)
    {
      return new Manifest();
    }
    return new Manifest(zipFile.getInputStream(manifestEntry));
  }

  public static byte[] computeDigest(ZipFile zipFile) throws NoSuchAlgorithmException, IOException
  {
    byte[] buffer = new byte[16*1024];
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
    while (zipEntries.hasMoreElements())
    {
      ZipEntry entry = zipEntries.nextElement();

      if (DIGEST.equals(entry.getName()) || SIGNATURE.equals(entry.getName()))
      {
        continue;
      }

      digest.update(
        entry.getName().getBytes("UTF-8")
      );
      updateDigest(buffer, digest, zipFile.getInputStream(entry));
    }

    return digest.digest();
  }

  private static void updateDigest(byte[] buffer, MessageDigest digest, InputStream inputStream) throws IOException
  {
    int read;
    while ( (read = inputStream.read(buffer)) > -1 )
    {
     digest.update(buffer, 0, read);
    }
    inputStream.close();
  }

  public static byte[] inputStreamToByteArray(InputStream inputStream, byte[] buffer) throws IOException
  {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    int read;
    while ( (read = inputStream.read(buffer)) > -1 )
    {
      outputStream.write(buffer, 0, read);
    }
    outputStream.close();

    return  outputStream.toByteArray();
  }

  public static byte[] decodeHexStringToByteArray(String hexEncoded)
  {
    if (hexEncoded.length() % 2 != 0)
    {
      throw new RuntimeException("Not an HEX encoded string");
    }

    byte[] buffer = new byte[hexEncoded.length() / 2];
    for (int i = 0; i < hexEncoded.length(); i += 2)
    {
      buffer[i/2] = (byte) Integer.parseInt(hexEncoded.substring(i, i + 2), 16);
    }

    return buffer;
  }
}
