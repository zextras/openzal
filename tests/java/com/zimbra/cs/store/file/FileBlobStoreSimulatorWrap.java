package com.zimbra.cs.store.file;

import com.zextras.lib.vfs.RelativePath;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.BlobBuilder;
import com.zimbra.cs.store.MailboxBlob;
import com.zimbra.cs.store.StagedBlob;
import com.zimbra.cs.store.StoreManager;
import com.zimbra.cs.volume.VolumeManager;

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
    throw new RuntimeException();
  }

  @Override
  public VolumeStagedBlob stage(Blob blob, Mailbox mbox) throws IOException
  {
    throw new RuntimeException();
  }

  @Override
  public VolumeMailboxBlob copy(MailboxBlob src, Mailbox destMbox, int destItemId, int destRevision) throws IOException, ServiceException
  {
    short volumeId = VolumeManager.getInstance().getCurrentMessageVolume().getId();
    return new MockVolumeMailboxBlob(mStore.copy(src, destMbox, destItemId, destRevision), volumeId);
  }

  @Override
  public VolumeMailboxBlob copy(Blob src, Mailbox destMbox, int destItemId, int destRevision, short destVolumeId) throws IOException, ServiceException
  {
    return new MockVolumeMailboxBlob(mStore.copy((StoreManagerSimulator.MockBlob) src, destMbox, destItemId, destRevision, String.valueOf(destVolumeId)), destVolumeId);
  }

  @Override
  public VolumeMailboxBlob link(StagedBlob src, Mailbox destMbox, int destItemId, int destRevision) throws IOException, ServiceException
  {
    short volumeId = VolumeManager.getInstance().getCurrentMessageVolume().getId();
    return new MockVolumeMailboxBlob(mStore.link(src, destMbox, destItemId, destRevision), volumeId);
  }

  @Override
  public VolumeMailboxBlob link(Blob src, Mailbox destMbox, int destItemId, int destRevision, short destVolumeId) throws IOException, ServiceException
  {
    return new MockVolumeMailboxBlob(mStore.link(src, destMbox, destItemId, destRevision), destVolumeId);
  }

  @Override
  public VolumeMailboxBlob renameTo(StagedBlob src, Mailbox destMbox, int destItemId, int destRevision) throws IOException, ServiceException
  {
    short volumeId = VolumeManager.getInstance().getCurrentMessageVolume().getId();
    return new MockVolumeMailboxBlob(mStore.renameTo(src, destMbox, destItemId, destRevision), volumeId);
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

  public static class MockVolumeMailboxBlob extends VolumeMailboxBlob
  {
    public MockVolumeMailboxBlob(MailboxBlob blob, short volumeId) throws IOException
    {
      super(blob.getMailbox(), blob.getItemId(), blob.getRevision(), blob.getLocator(), new MockVolumeBlob(blob.getLocalBlob(), volumeId));
    }
  }

  public static class MockVolumeBlob extends VolumeBlob
  {
    private final short mVolumeId;
    MockVolumeBlob(Blob blob, short volumeId)
    {
      super(blob.getFile(), volumeId);
      mVolumeId = volumeId;
    }

    public short getVolumeId()
    {
      return mVolumeId;
    }
  }
}
