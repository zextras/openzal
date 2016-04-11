package org.openzal.zal;

import com.zimbra.cs.store.file.BlobWrap;
import org.jetbrains.annotations.NotNull;

public class StagedBlobWrap implements StagedBlob
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

  public Blob getBlob()
  {
    return BlobWrap.wrap(mStagedBlob.getLocalBlob(), Short.parseShort(getLocator()));
  }

  @Override
  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mStagedBlob);
  }

  public static StagedBlobWrap wrap(Object stagedBlob)
  {
    return new StagedBlobWrap(stagedBlob);
  }
}
