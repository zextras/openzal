package org.openzal.zal.extension;

import org.openzal.zal.FileBlobStoreWrap;
import org.openzal.zal.PrimaryStore;
import org.openzal.zal.FileBlobPrimaryStore;
import org.openzal.zal.Store;
import org.openzal.zal.StoreBuilder;
import org.openzal.zal.StoreManager;
import org.openzal.zal.StoreVolume;
import org.openzal.zal.VolumeManager;
import org.openzal.zal.log.ZimbraLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class StoreManagerImpl implements StoreManager
{
  private final Map<String, Store>        mStores;
  private final Map<String, StoreBuilder> mStoreFactories;
  private final ReentrantLock             mLock;
  private final VolumeManager             mVolumeManager;
  private final StoreBuilder              mFileBlobStoreBuilder;

  public StoreManagerImpl(
    final Object fileBlobStore,
    VolumeManager volumeManager
  )
  {
    mVolumeManager = volumeManager;
    mLock = new ReentrantLock();
    mStoreFactories = new HashMap<String, StoreBuilder>();
    mStores = new HashMap<String, Store>();
    mFileBlobStoreBuilder = new StoreBuilder()
    {
      @Override
      public Store make(String volumeId)
      {
        return new FileBlobPrimaryStore(
          (FileBlobStoreWrap) fileBlobStore,
          mVolumeManager.getById(volumeId)
        );
      }
    };
  }

  @Override
  public void register(StoreBuilder storeBuilder, String volumeId)
  {
    mLock.lock();
    try
    {
      if (mVolumeManager.isValidVolume(volumeId))
      {
        mStoreFactories.put(volumeId, storeBuilder);
      }
      else
      {
        ZimbraLog.extensions.warn("Cannot register custom store for unknown volume " + volumeId);
      }
    }
    finally
    {
      mLock.unlock();
    }
  }

  @Override
  public void unregister(String volumeId)
  {
    mLock.lock();
    try
    {
      mStoreFactories.remove(volumeId);
      mStores.remove(volumeId);
    }
    finally
    {
      mLock.unlock();
    }
  }

  @Override
  public void makeActive(String volumeId)
  {
    mVolumeManager.setCurrentVolume(StoreVolume.TYPE_MESSAGE, Short.parseShort(volumeId));
  }

  @Override
  public void startup() throws IOException
  {
    for (Store store : mStores.values())
    {
      store.startup();
    }
  }

  @Override
  public void shutdown()
  {
    for (Store store : mStores.values())
    {
      store.shutdown();
    }
  }

  @Override
  public PrimaryStore getPrimaryStore()
  {
    Store store = getStore(mVolumeManager.getCurrentMessageVolume().getId());
    return store.toPrimaryStore();
  }

  @Override
  public Store getStore(String volumeId)
  {
    if (!mStores.containsKey(volumeId))
    {
      Store store;
      if (mStoreFactories.containsKey(volumeId))
      {
        store = mStoreFactories.get(volumeId).make(volumeId);
      }
      else
      {
        store = mFileBlobStoreBuilder.make(volumeId);
      }
      mStores.put(volumeId, store);
    }
    return mStores.get(volumeId);
  }

  @Override
  public Collection<Store> getAllStores()
  {
    List<Store> stores = new ArrayList<Store>();
    for (StoreVolume volume : mVolumeManager.getAll())
    {
      stores.add(getStore(volume.getId()));
    }
    return stores;
  }
}
