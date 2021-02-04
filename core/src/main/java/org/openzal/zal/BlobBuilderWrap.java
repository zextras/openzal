package org.openzal.zal;

import com.zimbra.common.service.ServiceException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;

public class BlobBuilderWrap implements BlobBuilder
{
  com.zimbra.cs.store.BlobBuilder mBlobBuilder;
  public BlobBuilderWrap( Object blobBuilder )
  {
    mBlobBuilder = (com.zimbra.cs.store.BlobBuilder) blobBuilder;
  }


  @Override
  public BlobBuilder setSizeHint(long size)
  {
    mBlobBuilder.setSizeHint(size);
    return this;
  }

  @Override
  public long getSizeHint()
  {
    return mBlobBuilder.getSizeHint();
  }

  @Override
  public long getTotalBytes()
  {
    return mBlobBuilder.getTotalBytes();
  }

  @Override
  public BlobBuilder disableCompression(boolean disable)
  {
    mBlobBuilder.disableCompression(disable);
    return this;
  }

  @Override
  public BlobBuilder disableDigest(boolean disable)
  {
    mBlobBuilder.disableDigest(disable);
    return this;
  }

  @Override
  public BlobBuilder init()
    throws IOException, ZimbraException
  {
    try
    {
      mBlobBuilder.init();
    }
    catch( ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
    return this;
  }

  @Override
  public BlobBuilder append(InputStream in)
    throws IOException
  {
    mBlobBuilder.append(in);
    return this;
  }

  @Override
  public BlobBuilder append(byte[] b, int off, int len)
    throws IOException
  {
    mBlobBuilder.append(b, off, len);
    return this;
  }

  @Override
  public BlobBuilder append(byte[] b)
    throws IOException
  {
    mBlobBuilder.append(b);
    return this;
  }

  @Override
  public BlobBuilder append(ByteBuffer bb)
    throws IOException
  {
    mBlobBuilder.append(bb);
    return this;
  }

  @Override
  public boolean isFinished()
  {
    return mBlobBuilder.isFinished();
  }

  @Override
  public void dispose()
  {
    mBlobBuilder.dispose();
  }

  @Override
  public Blob finish()
    throws IOException, ZimbraException
  {
    try
    {
      return BlobWrap.wrapZimbraBlob(mBlobBuilder.finish());
    }
    catch( ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public Blob getBlob()
  {
    return BlobWrap.wrapZimbraBlob(mBlobBuilder.getBlob());
  }

  @Override
  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mBlobBuilder);
  }
}
