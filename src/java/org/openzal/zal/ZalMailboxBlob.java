package org.openzal.zal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ZalMailboxBlob extends ZalBlob implements MailboxBlob
{
  private final Blob mBlob;
  private final Mailbox mMbox;
  private final int mMsgId;
  private final int mRevision;

  public ZalMailboxBlob(Blob blob, Mailbox mbox, int msgId, int revision)
  {
    super(blob.getFile(), blob.getVolumeId());
    mBlob = blob;
    mMbox = mbox;
    mMsgId = msgId;
    mRevision = revision;
  }

  @Override
  public int getRevision()
  {
    return mRevision;
  }

  @Override
  public int getItemId()
  {
    return mMsgId;
  }

  @Override
  public Mailbox getMailbox()
  {
    return mMbox;
  }

  @Override
  public Blob getLocalBlob()
  {
    return this;
  }

  @Override
  public void renameTo(String newPath) throws IOException
  {
    mBlob.renameTo(newPath);
  }

  @Override
  public String getKey()
  {
    return mBlob.getKey();
  }

  @Override
  public File getFile()
  {
    return mBlob.getFile();
  }

  @Override
  public <T> T toZimbra(Class<T> cls)
  {
    if (cls.equals(com.zimbra.cs.store.Blob.class))
    {
      return cls.cast(new InternalOverrideBlob(this));
    }
    return cls.cast(new InternalOverrideMailboxBlob(this));
  }

  @Override
  public String getDigest()
  {
    return mBlob.getDigest();
  }

  public long getSize() throws IOException
  {
    return mBlob.getSize();
  }

  @Override
  public String getVolumeId()
  {
    return mBlob.getVolumeId();
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    return mBlob.getInputStream();
  }

  @Override
  public boolean hasMailboxInfo()
  {
    return true;
  }

  @Override
  public MailboxBlob toMailboxBlob()
  {
    return this;
  }
}
