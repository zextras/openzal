package org.openzal.zal;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class StagedBlobWrap<S extends Blob> implements StagedBlob
{
  private final com.zimbra.cs.store.file.VolumeStagedBlob mStagedBlob;

  public StagedBlobWrap(@NotNull Object stagedBlob)
  {
    mStagedBlob = (com.zimbra.cs.store.file.VolumeStagedBlob) stagedBlob;
  }

  public Mailbox getMailbox()
  {
    return new Mailbox(mStagedBlob.getMailbox());
  }

  public long getSize()
  {
    /* $if ZimbraVersion >= 7.0.0 $ */
    return mStagedBlob.getSize();
    /* $else $
    return mStagedBlob.getStagedSize();
    /* $endif $ */
  }

  public String getLocator()
  {
    /* $if ZimbraVersion >= 7.0.0 $ */
    return mStagedBlob.getLocator();
    /* $else $
    return mStagedBlob.getStagedLocator();
    /* $endif $ */
  }

  public String getDigest()
  {
    /* $if ZimbraVersion >= 7.0.0 $ */
    return mStagedBlob.getDigest();
    /* $else $
    return mStagedBlob.getStagedDigest();
    /* $endif $ */
  }

  @Override
  public long getRawSize()
  {
    return 0;
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    return null;
  }

  @Override
  public void renameTo(String newPath) throws IOException
  {
    wrapZimbraObject(mStagedBlob).renameTo(newPath);
  }

  @Override
  public String getKey()
  {
    return wrapZimbraObject(mStagedBlob).getKey();
  }

  @Override
  public File getFile()
  {
    return wrapZimbraObject(mStagedBlob).getFile();
  }

  @Override
  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mStagedBlob);
  }

  @Override
  public String getVolumeId()
  {
    return null;
  }

  @Override
  public Blob getLocalBlob()
  {
    return BlobWrap.wrapZimbraObject(mStagedBlob.getLocalBlob(), mStagedBlob.getLocator());
  }

  public static StagedBlob wrapZimbraObject(Object stagedBlob)
  {
    if (stagedBlob instanceof InternalOverrideStagedBlob)
    {
      return ((InternalOverrideStagedBlob) stagedBlob).getWrappedObject();
    }

    return new StagedBlobWrap(stagedBlob);
  }
}
