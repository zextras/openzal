package org.openzal.zal;

import com.zimbra.cs.mailbox.Mailbox;

public class InternalOverrideStagedBlob extends com.zimbra.cs.store.StagedBlob
{
  private final StagedBlob mBlob;

  protected InternalOverrideStagedBlob(StagedBlob blob)
  {
    super(null, null, 0);
    mBlob = blob;
  }

  @Override
  public Mailbox getMailbox()
  {
    return mBlob.getMailbox().toZimbra(Mailbox.class);
  }

  @Override
  /* $if ZimbraVersion >= 7.0.0 $ */
  public long getSize()
  /* $else $
  public long getStagedSize()
  /* $endif $ */
  {
    return mBlob.getRawSize();
  }

  @Override
  /* $if ZimbraVersion >= 7.0.0 $ */
  public String getDigest()
  /* $else $
  public String getStagedDigest()
  /* $endif $ */
  {
    return mBlob.getDigest();
  }

  @Override
  /* $if ZimbraVersion >= 7.0.0 $ */
  public String getLocator()
  /* $else $
  public String getStagedLocator()
  /* $endif $ */
  {
    return mBlob.getVolumeId();
  }

  /* $if ZimbraVersion < 7.0.0 $
  @Override
  public long getOriginalSize()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getOriginalDigest()
  {
    throw new UnsupportedOperationException();
  }
  /* $endif $ */

  public static Object wrap(StagedBlob stagedBlob)
  {
    return new InternalOverrideStagedBlob(stagedBlob);
  }

  public StagedBlob getWrappedObject()
  {
    return mBlob;
  }
}
