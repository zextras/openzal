package org.openzal.zal;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.MailboxBlob;

import java.io.IOException;

class InternalOverrideMailboxBlob extends com.zimbra.cs.store.MailboxBlob
{
  private final org.openzal.zal.MailboxBlob mZalMailboxBlob;

  public InternalOverrideMailboxBlob(org.openzal.zal.MailboxBlob zalMailboxBlob)
  {
    super(null, 0, 0, null);
    mZalMailboxBlob = zalMailboxBlob;
  }

  @Override
  public int getItemId()
  {
    return mZalMailboxBlob.getItemId();
  }

  @Override
  public int getRevision()
  {
    return mZalMailboxBlob.getRevision();
  }

  @Override
  public String getLocator()
  {
    return mZalMailboxBlob.getVolumeId();
  }

  @Override
  public String getDigest() throws IOException
  {
    return mZalMailboxBlob.getDigest();
  }

  @Override
  public MailboxBlob setDigest(String digest)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getSize() throws IOException
  {
    return 0L;
  }

  @Override
  public MailboxBlob setSize(long size)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Mailbox getMailbox()
  {
    return null;
  }

  @Override
  public Blob getLocalBlob() throws IOException
  {
    return mZalMailboxBlob.getLocalBlob().toZimbra(Blob.class);
  }

  @Override
  public String toString()
  {
    return mZalMailboxBlob.toString();
  }

  public org.openzal.zal.MailboxBlob getWrappedMailboxBlob()
  {
    return mZalMailboxBlob;
  }

  public static Object wrap(org.openzal.zal.MailboxBlob mailboxBlob)
  {
    return new InternalOverrideMailboxBlob(mailboxBlob);
  }
}
