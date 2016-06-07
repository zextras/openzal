package com.zextras.powerstore;

import com.zextras.lib.vfs.ZxVFS;
import org.jetbrains.annotations.Nullable;
import org.openzal.zal.Blob;
import org.openzal.zal.Mailbox;
import org.openzal.zal.MailboxBlob;
import org.openzal.zal.PrimaryStore;
import org.openzal.zal.Store;
import org.openzal.zal.StoreFeature;
import org.openzal.zal.exceptions.ZimbraException;

import java.io.IOException;
import java.io.InputStream;

public class StoreAccessorWrapper implements Store
{
  private final StoreAccessor mStoreAccessor;

  public StoreAccessorWrapper(StoreAccessor storeAccessor)
  {
    mStoreAccessor = storeAccessor;
  }

  @Override
  public MailboxBlob copy(Blob src, Mailbox destMbox, int destMsgId, int destRevision) throws IOException
  {
    return null;
  }

  @Override
  public MailboxBlob link(Blob src, Mailbox destMbox, int destMsgId, int destRevision) throws IOException
  {
    return null;
  }

  @Override
  public boolean delete(Blob blob) throws IOException
  {
    return false;
  }

  @Override
  public void startup() throws IOException, ZimbraException
  {

  }

  @Override
  public void shutdown()
  {

  }

  @Override
  public boolean supports(StoreFeature feature)
  {
    return false;
  }

  @Override
  public InputStream getContent(Blob blob) throws IOException
  {
    return null;
  }

  @Override
  public MailboxBlob getMailboxBlob(Mailbox mbox, int msgId, int revision)
  {
    return null;
  }

  @Override
  public boolean delete(Mailbox mailbox, @Nullable Iterable blobs) throws IOException, ZimbraException
  {
    return false;
  }

  @Override
  public PrimaryStore toPrimaryStore()
  {
    return null;
  }

  @Override
  public String getVolumeId()
  {
    return null;
  }

  @Override
  public String getBlobPath(int mboxId, int itemId, int modContent)
  {
    return null;
  }
}
