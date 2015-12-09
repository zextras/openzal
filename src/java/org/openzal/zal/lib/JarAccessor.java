package org.openzal.zal.lib;

import org.openzal.zal.tools.JarUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarAccessor
{
  private static final String DIGEST      = "DIGEST";

  private final File     mFile;
  private       ZipFile  mZipFile;
  private       Manifest mManifest;

  public JarAccessor(String path)
  {
    mFile = new File(path);
    mZipFile = null;
    mManifest = null;
  }

  public JarAccessor(File file)
  {
    mFile = file;
    mZipFile = null;
    mManifest = null;
  }

  public ZipFile getZipFile() throws IOException
  {
    if (mZipFile == null)
    {
      mZipFile = new ZipFile(mFile);
    }

    return mZipFile;
  }

  public Manifest getManifest() throws IOException
  {
    if (mManifest == null)
    {
      mManifest = JarUtils.getManifest(getZipFile());
    }

    return mManifest;
  }

  public boolean hasAttributeInManifest(String key) throws IOException
  {
    return getManifest().getMainAttributes().containsKey(key);
  }

  public String getAttributeInManifest(String key) throws IOException
  {
    return getManifest().getMainAttributes().getValue(key);
  }

  public String getPath()
  {
    return mFile.getAbsolutePath();
  }

  public void validateDigest(boolean force) throws IOException, NoSuchAlgorithmException
  {
    String digest = new String(getDigest());
    if (!digest.isEmpty() || force)
    {
      if (digest.isEmpty())
      {
        throw new RuntimeException("No digest found in archive " + getPath());
      }

      String actualMD5 = JarUtils.printableByteArray(JarUtils.computeDigest(getZipFile()));
      if (! actualMD5.equalsIgnoreCase(digest))
      {
        throw new RuntimeException("Digest mismatch for file " + getPath() + "\n" +
                                   " expected " + digest + "\n" +
                                   " actual   " + actualMD5);
      }
    }
  }

  public byte[] getDigest() throws IOException
  {
    return getContent(DIGEST);
  }

  protected byte[] getContent(String entry) throws IOException
  {
    ZipEntry zipEntry = getZipFile().getEntry(entry);

    byte[] buffer = new byte[1024*10];
    if ( zipEntry == null )
    {
      return new byte[0];
    }

    InputStream digestContent = getZipFile().getInputStream(zipEntry);
    try
    {
      return JarUtils.inputStreamToByteArray(digestContent, buffer);
    }
    finally
    {
      digestContent.close();
    }
  }
}
