package org.openzal.zal.lib;

import org.openzal.zal.tools.JarUtils;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.jar.Manifest;
import java.util.zip.ZipFile;

public class JarAccessor
{
  private static final String ATTR_DIGEST       = "Digest";

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
    if (hasAttributeInManifest(ATTR_DIGEST) || force)
    {
      String digest = getAttributeInManifest(ATTR_DIGEST);
      String computedDigest = "";
      if (digest != null && !digest.isEmpty())
      {
        computedDigest = JarUtils.computeDigest(getZipFile());
      }

      if (computedDigest.isEmpty() || !computedDigest.equals(digest))
      {
        throw new RuntimeException("Digest mismatch for file " + getPath());
      }
    }
  }
}
