package org.openzal.zal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MailboxBlobImpl implements MailboxBlob
{
  private final Blob mBlob;
  private final Mailbox mMbox;
  private final int mMsgId;
  private final int mRevision;

  public MailboxBlobImpl(Blob blob, Mailbox mbox, int msgId, int revision)
  {

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
    return mBlob;
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
    return cls.cast(new InternalOverrideMailboxBlob(this));
  }

  @Override
  public String getDigest() throws IOException
  {
    return mBlob.getDigest();
  }

  @Override
  public long getRawSize() throws IOException
  {
    return mBlob.getRawSize();
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
}
