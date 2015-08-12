package com.zimbra.cs.store.file;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.BlobBuilder;
import com.zimbra.cs.store.IncomingBlob;
import com.zimbra.cs.store.MailboxBlob;
import com.zimbra.cs.store.StagedBlob;
import com.zimbra.cs.store.StoreManager;

import java.io.IOException;
import java.io.InputStream;

public class FileBlobStoreWrapImpl implements FileBlobStoreWrap
{
  private final FileBlobStore mStore;

  public static String getFilename(int itemId, int revision)
  {
    return FileBlobStore.getFilename(itemId, revision);
  }

  @Override
  public void startup() throws IOException, ServiceException
  {
    mStore.startup();
  }

  @Override
  public void shutdown()
  {
    mStore.shutdown();
  }

  @Override
  public boolean supports(StoreManager.StoreFeature feature)
  {
    return mStore.supports(feature);
  }

  @Override
  public BlobBuilder getBlobBuilder() throws IOException, ServiceException
  {
    return mStore.getBlobBuilder();
  }

  @Override
  public Blob storeIncoming(InputStream in, boolean storeAsIs) throws IOException, ServiceException
  {
    return mStore.storeIncoming(in, storeAsIs);
  }

  @Override
  public VolumeStagedBlob stage(InputStream in, long actualSize, Mailbox mbox) throws IOException, ServiceException
  {
    return mStore.stage(in, actualSize, mbox);
  }

  @Override
  public VolumeStagedBlob stage(Blob blob, Mailbox mbox) throws IOException
  {
    return mStore.stage(blob, mbox);
  }

  @Override
  public VolumeMailboxBlob copy(MailboxBlob src, Mailbox destMbox, int destItemId, int destRevision) throws IOException, ServiceException
  {
    return mStore.copy(src, destMbox, destItemId, destRevision);
  }

  @Override
  public VolumeMailboxBlob copy(Blob src, Mailbox destMbox, int destItemId, int destRevision, short destVolumeId) throws IOException, ServiceException
  {
    return mStore.copy(src, destMbox, destItemId, destRevision, destVolumeId);
  }

  @Override
  public VolumeMailboxBlob link(StagedBlob src, Mailbox destMbox, int destItemId, int destRevision) throws IOException, ServiceException
  {
    return mStore.link(src, destMbox, destItemId, destRevision);
  }

  @Override
  public VolumeMailboxBlob link(Blob src, Mailbox destMbox, int destItemId, int destRevision, short destVolumeId) throws IOException, ServiceException
  {
    return mStore.link(src, destMbox, destItemId, destRevision, destVolumeId);
  }

  @Override
  public VolumeMailboxBlob renameTo(StagedBlob src, Mailbox destMbox, int destItemId, int destRevision) throws IOException, ServiceException
  {
    return mStore.renameTo(src, destMbox, destItemId, destRevision);
  }

  @Override
  public boolean delete(MailboxBlob mblob) throws IOException
  {
    return mStore.delete(mblob);
  }

  @Override
  public boolean delete(StagedBlob staged) throws IOException
  {
    return mStore.delete(staged);
  }

  @Override
  public boolean delete(Blob blob) throws IOException
  {
    return mStore.delete(blob);
  }

  @Override
  public MailboxBlob getMailboxBlob(Mailbox mbox, int itemId, int revision, String locator) throws ServiceException
  {
    return mStore.getMailboxBlob(mbox, itemId, revision, locator);
  }

  @Override
  public InputStream getContent(MailboxBlob mboxBlob) throws IOException
  {
    return mStore.getContent(mboxBlob);
  }

  @Override
  public InputStream getContent(Blob blob) throws IOException
  {
    return mStore.getContent(blob);
  }

  @Override
  public boolean deleteStore(Mailbox mbox, Iterable<MailboxBlob.MailboxBlobInfo> blobs) throws IOException, ServiceException
  {
    return mStore.deleteStore(mbox, blobs);
  }

  public static String getBlobPath(Mailbox mbox, int itemId, int revision, short volumeId) throws ServiceException
  {
    return FileBlobStore.getBlobPath(mbox, itemId, revision, volumeId);
  }

  public static String getBlobPath(int mboxId, int itemId, int revision, short volumeId) throws ServiceException
  {
    return FileBlobStore.getBlobPath(mboxId, itemId, revision, volumeId);
  }

  public static void appendFilename(StringBuilder sb, int itemId, int revision)
  {
    FileBlobStore.appendFilename(sb, itemId, revision);
  }

  public FileBlobStoreWrapImpl(FileBlobStore store)
  {
    mStore = store;
  }
}
