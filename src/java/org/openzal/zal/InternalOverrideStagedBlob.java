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
  public long getSize()
  {
    return mBlob.getRawSize();
  }

  @Override
  public String getDigest()
  {
    return mBlob.getDigest();
  }

  @Override
  public String getLocator()
  {
    return mBlob.getVolumeId();
  }

  public static Object wrap(StagedBlob stagedBlob)
  {
    return new InternalOverrideStagedBlob(stagedBlob);
  }

  public StagedBlob getWrappedObject()
  {
    return mBlob;
  }
}
