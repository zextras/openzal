package org.openzal.zal.extension;

import org.jetbrains.annotations.Nullable;
import org.openzal.zal.Blob;
import org.openzal.zal.BlobBuilder;
import org.openzal.zal.Mailbox;
import org.openzal.zal.MailboxBlob;
import org.openzal.zal.PrimaryStoreAccessor;
import org.openzal.zal.StagedBlob;
import org.openzal.zal.StoreAccessor;
import org.openzal.zal.StoreAccessorFactory;
import org.openzal.zal.StoreFeature;
import org.openzal.zal.StoreManager;
import org.openzal.zal.exceptions.ZimbraException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;


public class StoreManagerImpl implements StoreManager
{
  private final PrimaryStoreAccessor                 mDefaultStoreAccessor;
  private final HashMap<Short, StoreAccessor>        mStoreAccessors;
  private final HashMap<Short, StoreAccessorFactory> mStoreAccessorFactories;
  private final ReentrantLock                        mLock;

  public StoreManagerImpl(
    PrimaryStoreAccessor defaultStoreAccessor
  )
  {
    mLock = new ReentrantLock();
    mDefaultStoreAccessor = defaultStoreAccessor;
    mStoreAccessorFactories = new HashMap<Short, StoreAccessorFactory>();
    mStoreAccessors = new HashMap<Short, StoreAccessor>();
  }

  private StoreAccessor selectStoreAccessor(String locator)
  {
    /* TODO handle primaryActive -> mDefaultStoreAccessor
     * handle secondaryActive.. ??
     */
    return selectStoreAccessor(Short.parseShort(locator));
  }

  private StoreAccessor selectStoreAccessor(short id)
  {
    mLock.lock();
    try
    {
      if (!mStoreAccessors.containsKey(id))
      {
        mStoreAccessors.put(id, mStoreAccessorFactories.get(id).make(id));
      }

      return mStoreAccessors.get(id);
    }
    finally
    {
      mLock.unlock();
    }
  }

  @Override
  public void registerStoreAccessor(StoreAccessorFactory storeAccessorFactory, short volumeId)
  {
    mLock.lock();
    try
    {
      mStoreAccessors.put(volumeId, storeAccessorFactory.make(volumeId));
      mStoreAccessorFactories.put(volumeId, storeAccessorFactory);
    }
    finally
    {
      mLock.unlock();
    }
  }

  @Override
  public void invalidateStoreAccessor(Collection<Short> volumes)
  {
    mLock.lock();
    try
    {
      for (short volumeId : volumes)
      {
        mStoreAccessors.remove(volumeId);
      }
    }
    finally
    {
      mLock.unlock();
    }
  }

  @Override
  public Blob storeIncoming(InputStream data, boolean storeAsIs) throws IOException, ZimbraException
  {
    return mDefaultStoreAccessor.storeIncoming(data, storeAsIs);
  }

  @Override
  public StagedBlob stage(Blob blob, Mailbox mbox) throws IOException
  {
    return mDefaultStoreAccessor.stage(blob, mbox);
  }

  @Nullable
  @Override
  public MailboxBlob getMailboxBlob(Mailbox mbox, int msgId, int revision, String locator) throws ZimbraException
  {
    return selectStoreAccessor(locator).getMailboxBlob(mbox, msgId, revision, locator);
  }

  @Override
  public MailboxBlob copy(Blob src, Mailbox destMbox, int destMsgId, int destRevision) throws IOException, ZimbraException
  {
    return mDefaultStoreAccessor.copy(src, destMbox, destMsgId, destRevision);
  }

  @Override
  public MailboxBlob copy(Blob src, Mailbox destMbox, int destMsgId, int destRevision, short volumeId) throws IOException, ZimbraException
  {
    return selectStoreAccessor(volumeId).copy(src, destMbox, destMsgId, destRevision, volumeId);
  }

  @Override
  public MailboxBlob link(Blob src, Mailbox destMbox, int destMsgId, int destRevision) throws IOException, ZimbraException
  {
    return mDefaultStoreAccessor.link(src, destMbox, destMsgId, destRevision);
  }

  @Override
  public MailboxBlob link(Blob src, Mailbox destMbox, int destMsgId, int destRevision, short volumeId) throws IOException, ZimbraException
  {
    return selectStoreAccessor(volumeId).link(src, destMbox, destMsgId, destRevision, volumeId);
  }

  @Override
  public String getBlobPath(int mboxId, int itemId, int revision, short volumeId) throws ZimbraException
  {
    return selectStoreAccessor(volumeId).getBlobPath(mboxId, itemId, revision, volumeId);
  }

  @Override
  public boolean delete(Mailbox mailbox) throws IOException
  {
    boolean deleted = false;
    deleted |= mDefaultStoreAccessor.delete(mailbox);
    for (StoreAccessor store : mStoreAccessors.values())
    {
      deleted |= store.delete(mailbox);
    }
    return deleted;
  }

  @Override
  public boolean delete(Blob blob) throws IOException
  {
    return selectStoreAccessor(blob.getVolumeId()).delete(blob);
  }

  @Override
  public boolean delete(Blob blob, String volumeId) throws IOException
  {
    return selectStoreAccessor(volumeId).delete(blob);
  }

  @Override
  public void startup() throws IOException, ZimbraException
  {
    mDefaultStoreAccessor.startup();
    for (StoreAccessor store : mStoreAccessors.values())
    {
      store.startup();
    }
  }

  @Override
  public void shutdown()
  {
    mDefaultStoreAccessor.shutdown();
    for (StoreAccessor store : mStoreAccessors.values())
    {
      store.shutdown();
    }
  }

  @Override
  public boolean supports(StoreFeature feature)
  {
    return mDefaultStoreAccessor.supports(feature);
  }

  @Override
  public BlobBuilder getBlobBuilder(short volumeId) throws IOException, ZimbraException
  {
    return selectStoreAccessor(volumeId).getBlobBuilder(volumeId);
  }

  @Nullable
  @Override
  public InputStream getContent(Blob blob, String volumeId) throws IOException
  {
    return selectStoreAccessor(volumeId).getContent(blob);
  }

  @Nullable
  @Override
  public InputStream getContent(Blob blob) throws IOException
  {
    return mDefaultStoreAccessor.getContent(blob);
  }

  @Override
  public MailboxBlob renameTo(StagedBlob src, Mailbox destMbox, int destMsgId, int destRevision) throws IOException
  {
    return mDefaultStoreAccessor.renameTo(src, destMbox, destMsgId, destRevision);
  }
}
