package com.zimbra.cs.store.file;

import com.zimbra.cs.store.Blob;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class InternalOverrideBlob extends com.zimbra.cs.store.Blob
{
  private final org.openzal.zal.Blob mBlob;

  @Override
  public File getFile()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getPath()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isCompressed() throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getDigest() throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getRawSize() throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Blob setCompressed(boolean isCompressed)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Blob setDigest(String digest)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Blob setRawSize(long rawSize)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Blob copyCachedDataFrom(Blob other)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  /* $if ZimbraVersion >= 7.1.3 $ */
  public void renameTo(String newPath) throws IOException
  {
    mBlob.renameTo(newPath);
  }
  /* $elseif ZimbraVersion >= 7.0.0 $
  public boolean renameTo(String newPath)
  {
    try
    {
      mBlob.renameTo(newPath);
      return true;
    }
    catch (IOException e)
    {
      return false;
    }
  }
  /* $elseif ZimbraVersion >= 6.0.15 $
  public void renameTo(String newPath) throws IOException
  {
    mBlob.renameTo(newPath);
  }
  /* $else $
  public boolean renameTo(String newPath)
  {
    try
    {
      mBlob.renameTo(newPath);
      return true;
    }
    catch (IOException e)
    {
      return false;
    }
  }
  /* $endif $ */

  @Override
  public String toString()
  {
    return super.toString();
  }

  public InternalOverrideBlob(org.openzal.zal.Blob blob)
  {
    super(new File("/tmp/fake"));
    mBlob = blob;
  }

  public Object getWrappedObject()
  {
    return mBlob;
  }

  public static Object wrap(org.openzal.zal.Blob src)
  {
    if (src instanceof BlobWrap)
      return src.toZimbra(Blob.class);

    if (src instanceof Blob)
      return src;

    return new InternalOverrideBlob(src);
  }
}
