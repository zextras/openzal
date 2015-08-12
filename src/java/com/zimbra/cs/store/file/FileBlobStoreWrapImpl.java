package com.zimbra.cs.store.file;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.BlobBuilder;
import com.zimbra.cs.store.MailboxBlob;
import com.zimbra.cs.store.StagedBlob;
import com.zimbra.cs.store.StoreManager;
/* $if ZimbraVersion < 8.0.0 $
import com.zimbra.cs.store.StorageCallback;
/* $endif $ */

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

  /* $if ZimbraVersion >= 7.2.0 $ */
  @Override
  public boolean supports(StoreManager.StoreFeature feature)
  {
    return mStore.supports(feature);
  }
  /* $endif $ */

  @Override
  public BlobBuilder getBlobBuilder() throws IOException, ServiceException
  {
    return mStore.getBlobBuilder();
  }

  @Override
  /* $if ZimbraVersion >= 8.0.0 $ */
  public Blob storeIncoming(InputStream in, boolean storeAsIs) throws IOException, ServiceException
  {
    return mStore.storeIncoming(in, storeAsIs);
  }
  /* $elseif ZimbraVersion >= 7.0.0 $
  public Blob storeIncoming(InputStream in, StorageCallback callback, boolean storeAsIs) throws IOException, ServiceException
  {
    return mStore.storeIncoming(in, callback, storeAsIs);
  }
  /* $else $
  public Blob storeIncoming(InputStream in, long sizeHint, StorageCallback callback, boolean storeAsIs) throws IOException, ServiceException
  {
    return mStore.storeIncoming(in, sizeHint, callback, storeAsIs);
  }
  /* $endif $ */

  @Override
  /* $if ZimbraVersion >= 8.0.0 $ */
  public VolumeStagedBlob stage(InputStream in, long actualSize, Mailbox mbox) throws IOException, ServiceException
  {
    return mStore.stage(in, actualSize, mbox);
  }
  /* $else $
  public VolumeStagedBlob stage(InputStream in, long actualSize, StorageCallback callback, Mailbox mbox) throws IOException, ServiceException
  {
    return mStore.stage(in, actualSize, callback, mbox);
  }
  /* $endif $ */

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
  /* $if ZimbraVersion >= 7.2.1 $ */
  public boolean deleteStore(Mailbox mbox, Iterable<MailboxBlob.MailboxBlobInfo> blobs) throws IOException, ServiceException
  {
    return mStore.deleteStore(mbox, blobs);
  }
  /* $elseif ZimbraVersion >= 7.2.0 $
  public boolean deleteStore(Mailbox mbox, Iterable<MailboxBlob> blobs) throws IOException, ServiceException
  {
    return mStore.deleteStore(mbox, blobs);
  }
  /* $else $
  public boolean deleteStore(Mailbox mbox) throws IOException, ServiceException
  {
    return mStore.deleteStore(mbox);
  }
  /* $endif $ */

  /*
  public static String getBlobPath(int mboxId, int itemId, int revision, short volumeId) throws ServiceException
  {
    return FileBlobStore.getBlobPath(mboxId, itemId, revision, volumeId);
  }
  */
  public static String getBlobPath(Mailbox mbox, int itemId, int revision, short volumeId) throws ServiceException
  {
    return FileBlobStore.getBlobPath(mbox, itemId, revision, volumeId);
  }

  /*
  public static void appendFilename(StringBuilder sb, int itemId, int revision)
  {
    FileBlobStore.appendFilename(sb, itemId, revision);
  }
  */

  public FileBlobStoreWrapImpl(FileBlobStore store)
  {
    mStore = store;
  }
}
