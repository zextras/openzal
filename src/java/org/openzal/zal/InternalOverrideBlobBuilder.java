package org.openzal.zal;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.store.Blob;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

class InternalOverrideBlobBuilder extends com.zimbra.cs.store.BlobBuilder
{
  private final BlobBuilder mBlobBuilder;

  public InternalOverrideBlobBuilder(BlobBuilder blobBuilder)
  {
    super(null);
    mBlobBuilder = blobBuilder;
  }

  public BlobBuilder getWrappedObject()
  {
    return mBlobBuilder;
  }

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
  public com.zimbra.cs.store.BlobBuilder init() throws IOException, ServiceException
  {
    mBlobBuilder.init();
    return this;
  }

  @Override
  public com.zimbra.cs.store.BlobBuilder append(InputStream in) throws IOException
  {
    mBlobBuilder.append(in);
    return this;
  }

  @Override
  public com.zimbra.cs.store.BlobBuilder append(byte[] b, int off, int len) throws IOException
  {
    mBlobBuilder.append(b, off, len);
    return this;
  }

  @Override
  public com.zimbra.cs.store.BlobBuilder append(byte[] b) throws IOException
  {
    mBlobBuilder.append(b);
    return this;
  }

  @Override
  public com.zimbra.cs.store.BlobBuilder append(ByteBuffer bb) throws IOException
  {
    mBlobBuilder.append(bb);
    return this;
  }

  @Override
  // TODO return VolumeBlob??
  public Blob finish() throws IOException, ServiceException
  {
    return mBlobBuilder.finish().toZimbra(com.zimbra.cs.store.Blob.class);
  }

  @Override
  public String toString()
  {
    return mBlobBuilder.toString();
  }

  @Override
  public Blob getBlob()
  {
    return mBlobBuilder.getBlob().toZimbra(com.zimbra.cs.store.Blob.class);
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

  public static com.zimbra.cs.store.BlobBuilder wrap(BlobBuilder blobBuilder)
  {
    if (blobBuilder instanceof BlobBuilderWrap)
    {
      return (com.zimbra.cs.store.BlobBuilder) ((BlobBuilderWrap)blobBuilder).getWrappedObject();
    }
    return new InternalOverrideBlobBuilder(blobBuilder);
  }
}
