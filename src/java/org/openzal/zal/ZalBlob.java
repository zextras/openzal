package org.openzal.zal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ZalBlob implements Blob
{
  private final File   mFile;
  private final String mVolumeId;
  private       String mDigest;
  private       Long   mRawSize;

  public ZalBlob(File file, String volumeId)
  {
    this(file, volumeId, null, null);
  }

  public ZalBlob(File file, String volumeId, String digest)
  {
    this(file, volumeId, digest, null);
  }

  public ZalBlob(File file, String volumeId, String digest, Long rawSize)
  {
    mFile = file;
    mVolumeId = volumeId;
    mDigest = digest;
    mRawSize = rawSize;
  }

  @Override
  public void renameTo(String newPath) throws IOException
  {
    boolean success = mFile.renameTo(new File(newPath));
    if (!success)
    {
      throw new IOException("Cannot rename " + mFile.getPath() + " to " + newPath);
    }
  }

  @Override
  public String getKey()
  {
    return mFile.getAbsolutePath();
  }

  @Override
  public File getFile()
  {
    return mFile;
  }

  @Override
  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(new InternalOverrideBlob(this));
  }

  @Override
  public String getDigest() throws IOException
  {
    return mDigest;
  }

  public long getSize() throws IOException
  {
    if (mRawSize == null)
    {
      mRawSize = mFile.length();
    }
    return mRawSize;
  }

  @Override
  public String getVolumeId()
  {
    return mVolumeId;
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    return new FileInputStream(mFile);
  }

  @Override
  public boolean hasMailboxInfo()
  {
    return false;
  }

  @Override
  public MailboxBlob toMailboxBlob()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setDigest(String digest)
  {
    mDigest = digest;
  }

  @Override
  public void setSize(long size)
  {
    mRawSize = size;
  }
}
