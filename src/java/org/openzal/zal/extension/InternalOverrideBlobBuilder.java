package org.openzal.zal.extension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.openzal.zal.BlobBuilder;
import org.openzal.zal.exceptions.ZimbraException;

public class InternalOverrideBlobBuilder extends com.zimbra.cs.store.BlobBuilder
{
  private final BlobBuilder mBlobBuilder;

  @Override
  public com.zimbra.cs.store.BlobBuilder setSizeHint(long size)
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
  public com.zimbra.cs.store.BlobBuilder disableCompression(boolean disable)
  {
    mBlobBuilder.disableCompression(disable);
    return this;
  }

  @Override
  public com.zimbra.cs.store.BlobBuilder disableDigest(boolean disable)
  {
    mBlobBuilder.disableDigest(disable);
    return this;
  }

  @Override
  public com.zimbra.cs.store.BlobBuilder init()
    throws IOException, ZimbraException
  {
    mBlobBuilder.init();
    return this;
  }

  @Override
  public com.zimbra.cs.store.BlobBuilder append(InputStream in)
    throws IOException
  {
    mBlobBuilder.append(in);
    return this;
  }

  @Override
  public com.zimbra.cs.store.BlobBuilder append(byte[] b, int off, int len)
    throws IOException
  {
    mBlobBuilder.append(b, off, len);
    return this;
  }

  @Override
  public com.zimbra.cs.store.BlobBuilder append(byte[] b)
    throws IOException
  {
    mBlobBuilder.append(b);
    return this;
  }

  @Override
  public com.zimbra.cs.store.BlobBuilder append(ByteBuffer bb)
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
  public com.zimbra.cs.store.Blob finish()
    throws IOException, ZimbraException
  {
    return mBlobBuilder.finish().toZimbra(com.zimbra.cs.store.Blob.class);
  }

  @Override
  public com.zimbra.cs.store.Blob getBlob()
  {
    return mBlobBuilder.getBlob().toZimbra(com.zimbra.cs.store.Blob.class);
  }

  public InternalOverrideBlobBuilder(BlobBuilder blobBuilder)
  {
    super(null);
    mBlobBuilder = blobBuilder;
  }


  public static Object wrap(BlobBuilder builder)
  {
    return new InternalOverrideBlobBuilder(builder);
  }
}
