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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.RandomAccessFile;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.zip.ZipFile;

public class ChecksumWriter
{
  public static void main(@NotNull String args[]) throws Exception
  {
    if( args.length <= 2 )
    {
      System.err.println("/path/to/PrivateKey, /path/to/Source, /path/to/Destination parameters required.");
      System.exit(1);
    }

    RandomAccessFile privateKeyFile = null;
    ZipFile zipFile = null;
    File destination;
    try
    {
      privateKeyFile = new RandomAccessFile(args[0], "r");
      zipFile = new ZipFile(args[1]);
      destination = new File(args[2]);

      byte[] privateKeyContent = new byte[(int) privateKeyFile.length()];
      try
      {
        privateKeyFile.readFully(privateKeyContent);
      }
      finally
      {
        privateKeyFile.close();
      }

      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      KeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyContent);
      RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

      byte[] currentDigest = JarUtils.computeDigest(zipFile);

      Signature rsa256 = Signature.getInstance("SHA256withRSA");
      rsa256.initSign(privateKey);
      rsa256.update(currentDigest);

      JarUtils.copyJar(zipFile, destination, currentDigest, rsa256.sign());

      System.exit(0);
    }
    finally
    {
      if (zipFile != null)
      {
        zipFile.close();
      }
      if (privateKeyFile != null)
      {
        privateKeyFile.close();
      }
    }

    System.exit(1);
  }
}
