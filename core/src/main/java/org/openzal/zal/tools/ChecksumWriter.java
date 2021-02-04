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

import javax.annotation.Nonnull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.zip.ZipFile;

public class ChecksumWriter
{
  public static void main(@Nonnull String args[]) throws Exception
  {
    String privateKeyPath = null;
    String zipFilePath = null;
    String outDir = null;
    File destination = null;
    if (args.length == 4 && "-d".equals(args[0]))
    {
      outDir = args[1];
      privateKeyPath = args[2];
      zipFilePath = args[3];
    }
    else if (args.length == 3)
    {
      privateKeyPath = args[0];
      zipFilePath = args[1];
      destination = new File(args[2]);
    }
    else
    {
      System.err.println("-d /path/, /path/to/PrivateKey, /path/to/Source or /path/to/PrivateKey, /path/to/Source, /path/to/Destination parameters required.");
      System.exit(1);
    }

    RandomAccessFile privateKeyFile = null;
    ZipFile zipFile = null;
    try
    {
      privateKeyFile = new RandomAccessFile(privateKeyPath, "r");
      zipFile = new ZipFile(zipFilePath);

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

      if (outDir != null)
      {
        File digest = new File(outDir + "/DIGEST");
        File signature = new File(outDir + "/SIGNATURE");
        FileOutputStream digestOutputStream = new FileOutputStream(digest);
        try
        {
          digestOutputStream.write(JarUtils.printableByteArray(currentDigest).getBytes());
        }
        finally
        {
          digestOutputStream.close();
        }

        FileOutputStream signatureOutputStream = new FileOutputStream(signature);
        try
        {
          signatureOutputStream.write(JarUtils.printableByteArray(rsa256.sign()).getBytes());
        }
        finally
        {
          signatureOutputStream.close();
        }
      }
      else
      {
        JarUtils.copyJar(zipFile, destination, currentDigest, rsa256.sign());
      }

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
