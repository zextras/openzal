package org.openzal.zal;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ZalBlob implements Blob
{
  private final File mFile;
  private final String mVolumeId;
  private final String mDigest;
  private final long mRawSize;

  public ZalBlob(File file, String volumeId, String digest, long rawSize)
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
      throw new IOException();
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
  public String getDigest()
  {
    return mDigest;
  }

  @Override
  public long getRawSize()
  {
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
}
