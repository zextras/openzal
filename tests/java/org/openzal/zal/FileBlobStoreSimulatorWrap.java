package org.openzal.zal;

import com.zextras.lib.vfs.RelativePath;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.BlobBuilder;
import com.zimbra.cs.store.MailboxBlob;
import com.zimbra.cs.store.StagedBlob;
import com.zimbra.cs.store.StoreManager;
import com.zimbra.cs.store.file.StoreManagerSimulator;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.cs.store.file.VolumeMailboxBlob;
import com.zimbra.cs.store.file.VolumeStagedBlob;
/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.cs.volume.VolumeManager;
/* $else $
import com.zimbra.cs.store.StorageCallback;
import com.zimbra.cs.store.file.Volume;
/* $endif $ */

import java.io.IOException;
import java.io.InputStream;

public class FileBlobStoreSimulatorWrap implements FileBlobStoreWrap
{
  private final StoreManagerSimulator mStore;

  public FileBlobStoreSimulatorWrap(StoreManagerSimulator store)
  {
    mStore = store;
  }

  @Override
  public void startup() throws IOException
  {
    mStore.startup();
  }

  @Override
  public void shutdown()
  {
    mStore.shutdown();
  }

  @Override
  public boolean supports(Object feature)
  {
    return mStore.supports((StoreManager.StoreFeature) feature);
  }

  @Override
  public BlobBuilder getBlobBuilder() throws IOException
  {
    return mStore.getBlobBuilder();
  }

  @Override
  public Blob storeIncoming(InputStream in, boolean storeAsIs) throws IOException
  {
    return mStore.storeIncoming(in, storeAsIs);
  }

  @Override
  public VolumeStagedBlob stage(InputStream in, long actualSize, Mailbox mbox) throws IOException
  {
    throw new RuntimeException();
  }

  @Override
  public VolumeStagedBlob stage(Blob blob, Mailbox mbox) throws IOException
  {
    String volumeId = String.valueOf(VolumeManager.getInstance().getCurrentMessageVolume().getId());
    return new StoreManagerSimulator.MockVolumeStagedBlob(mbox, (StoreManagerSimulator.MockBlob) blob, volumeId);
  }

  @Override
  public VolumeMailboxBlob copy(MailboxBlob src, Mailbox destMbox, int destItemId, int destRevision) throws IOException
  {
    String volumeId = String.valueOf(VolumeManager.getInstance().getCurrentMessageVolume().getId());
    return new StoreManagerSimulator.MockVolumeMailboxBlob(mStore.copy(src, destMbox, destItemId, destRevision), volumeId);
  }

  @Override
  public VolumeMailboxBlob copy(Blob src, Mailbox destMbox, int destItemId, int destRevision, String destVolumeId) throws IOException
  {
    return new StoreManagerSimulator.MockVolumeMailboxBlob(mStore.copy(StoreManagerSimulator.MockBlob.getMockBlob(src), destMbox, destItemId, destRevision, String.valueOf(destVolumeId)), destVolumeId);
  }

  @Override
  public VolumeMailboxBlob link(StagedBlob src, Mailbox destMbox, int destItemId, int destRevision) throws IOException
  {
    String volumeId = String.valueOf(VolumeManager.getInstance().getCurrentMessageVolume().getId());
    return new StoreManagerSimulator.MockVolumeMailboxBlob(mStore.link(src, destMbox, destItemId, destRevision), volumeId);
  }

  @Override
  public VolumeMailboxBlob link(Blob src, Mailbox destMbox, int destItemId, int destRevision, String destVolumeId) throws IOException
  {
    try
    {
      return new StoreManagerSimulator.MockVolumeMailboxBlob(mStore.link(src, destMbox, destItemId, destRevision), destVolumeId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public VolumeMailboxBlob renameTo(StagedBlob src, Mailbox destMbox, int destItemId, int destRevision) throws IOException
  {
    String volumeId = String.valueOf(VolumeManager.getInstance().getCurrentMessageVolume().getId());
    return new StoreManagerSimulator.MockVolumeMailboxBlob(mStore.renameTo(src, destMbox, destItemId, destRevision), volumeId);
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
  public MailboxBlob getMailboxBlob(Mailbox mbox, int itemId, int revision, String locator)
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
  public boolean deleteStore(Mailbox mbox, Iterable blobs) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object getWrappedObject()
  {
    throw new UnsupportedOperationException();
  }

  public RelativePath getBlobPath(
    long mboxId,
    int itemId,
    int revision,
    short volumeId
  )
  {
    return mStore.getBlobPath(mboxId, itemId, revision, volumeId);
  }

}
