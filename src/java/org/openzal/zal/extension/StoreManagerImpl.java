package org.openzal.zal.extension;

import com.zextras.lib.Promise;
import com.zextras.lib.PromiseBooleanAggregator;
import io.netty.util.concurrent.Future;
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
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


public class StoreManagerImpl implements StoreManager
{
  private final PrimaryStoreAccessor mDefaultStoreAccessor;
  private final HashMap<String, StoreAccessor> mStoreAccessors;
  private final HashMap<String, StoreAccessorFactory> mStoreAccessorFactories;

  private final ReentrantLock mLock;

  public StoreManagerImpl(
    PrimaryStoreAccessor defaultStoreAccessor
  )
  {
    mLock = new ReentrantLock();
    mDefaultStoreAccessor = defaultStoreAccessor;
    mStoreAccessorFactories = new HashMap<String, StoreAccessorFactory>();
    mStoreAccessors = new HashMap<String, StoreAccessor>();
  }

  private StoreAccessor selectStoreAccessor(String id)
  {
    mLock.lock();
    try
    {
      if (!mStoreAccessors.containsKey(id))
      {
        if (mStoreAccessorFactories.containsKey(id))
        {
          mStoreAccessors.put(id, mStoreAccessorFactories.get(id).make(id));
        }
        else
        {
          mStoreAccessors.put(id, mDefaultStoreAccessor);
        }
      }

      return mStoreAccessors.get(id);
    }
    finally
    {
      mLock.unlock();
    }
  }

  @Override
  public void registerStoreAccessor(StoreAccessorFactory storeAccessorFactory, String volumeId)
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
  public void invalidateStoreAccessor(Collection<String> volumes)
  {
    mLock.lock();
    try
    {
      for (String volumeId : volumes)
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
  public Future<Blob> storeIncoming(InputStream data, boolean storeAsIs) throws ZimbraException
  {
    return mDefaultStoreAccessor.storeIncoming(data, storeAsIs);
  }

  @Override
  public Future<StagedBlob> stage(Blob blob, Mailbox mbox)
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
  public Future<MailboxBlob> copy(Blob src, Mailbox destMbox, int destMsgId, int destRevision)
  {
    return mDefaultStoreAccessor.copy(src, destMbox, destMsgId, destRevision);
  }

  @Override
  public Future<MailboxBlob> copy(Blob src, Mailbox destMbox, int destMsgId, int destRevision, String volumeId)
  {
    return selectStoreAccessor(volumeId).copy(src, destMbox, destMsgId, destRevision, volumeId);
  }

  @Override
  public Future<MailboxBlob> link(Blob src, Mailbox destMbox, int destMsgId, int destRevision)
  {
    return mDefaultStoreAccessor.link(src, destMbox, destMsgId, destRevision);
  }

  @Override
  public Future<MailboxBlob> link(Blob src, Mailbox destMbox, int destMsgId, int destRevision, String volumeId)
  {
    return selectStoreAccessor(volumeId).link(src, destMbox, destMsgId, destRevision, volumeId);
  }

  @Override
  public String getBlobPath(int mboxId, int itemId, int revision, String volumeId) throws ZimbraException
  {
    return selectStoreAccessor(volumeId).getBlobPath(mboxId, itemId, revision, volumeId);
  }

  @Override
  public Future<Boolean> delete(Mailbox mailbox, Iterable blobs) throws IOException
  {
    Promise<Boolean> future = new Promise<Boolean>();
    PromiseBooleanAggregator futureListener = new PromiseBooleanAggregator(future);
    for (Map.Entry<String, StoreAccessor> entry : mStoreAccessors.entrySet())
    {
      entry.getValue().delete(mailbox, blobs, entry.getKey()).addListener(
        futureListener.incWaitingCount()
      );
    }
    futureListener.requestsDone();

    return future;
  }

  @Override
  public Future<Boolean> delete(Mailbox mailbox, Iterable blobs, String volumeId)
  {
    return selectStoreAccessor(volumeId).delete(mailbox, blobs, volumeId);
  }

  @Override
  public Future<Boolean> delete(MailboxBlob blob)
  {
    return selectStoreAccessor(blob.getVolumeId()).delete(blob);
  }

  @Override
  public Future<Boolean> delete(StagedBlob blob)
  {
    return selectStoreAccessor(blob.getVolumeId()).delete(blob);
  }

  @Override
  public Future<Boolean> delete(Blob blob)
  {
    return mDefaultStoreAccessor.delete(blob);
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
  public BlobBuilder getBlobBuilder() throws IOException, ZimbraException
  {
    return mDefaultStoreAccessor.getBlobBuilder();
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
  public Future<MailboxBlob> renameTo(StagedBlob src, Mailbox destMbox, int destMsgId, int destRevision)
  {
    return mDefaultStoreAccessor.renameTo(src, destMbox, destMsgId, destRevision);
  }
}
