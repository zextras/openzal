/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2016 ZeXtras S.r.l.
 *
 * This file is part of ZAL.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZAL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.openzal.zal.extension;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.commons.io.IOUtils;
import org.openzal.zal.CacheableStoreBuilder;
import org.openzal.zal.FileBlobPrimaryStore;
import org.openzal.zal.FileBlobStoreWrap;
import org.openzal.zal.PrimaryStore;
import org.openzal.zal.PrimaryStoreBuilder;
import org.openzal.zal.Store;
import org.openzal.zal.StoreManager;
import org.openzal.zal.StoreVolume;
import org.openzal.zal.VolumeManager;
import org.openzal.zal.log.ZimbraLog;

public class StoreManagerImpl implements StoreManager
{
  private final Map<String, Store> mStoresCached;
  private final Map<String, CacheableStoreBuilder> mCacheableStoreBuilderMap;
  private final ReentrantLock             mLock;
  private final VolumeManager             mVolumeManager;
  private final FileBlobStoreWrap mFileBlobStore;

  private PrimaryStoreBuilder mPrimaryStoreBuilder;

  static
  {
    try
    {
      Method defineClassMethod = ClassLoader.class.getDeclaredMethod(
        "defineClass", byte[].class, int.class, int.class
      );
      defineClassMethod.setAccessible(true);

      InputStream is = null;
      try
      {
        Class<?> parentClass = Class.forName("com.zimbra.cs.store.file.VolumeBlob");
        ClassLoader parentClassLoader = parentClass.getClassLoader();

        is = BootstrapClassLoader.class.getResourceAsStream("/com/zimbra/cs/store/file/VolumeBlobProxy");
        byte[] buffer = new byte[6 * 1024];
        int idx = 0;
        int read = 0;
        while (read > -1)
        {
          idx += read;
          if (buffer.length == idx)
          {
            buffer = Arrays.copyOf(buffer, buffer.length * 2);
          }
          read = is.read(buffer, idx, buffer.length - idx);
        }

        defineClassMethod.invoke(
          parentClassLoader,
          buffer, 0, idx
        );
      }
      catch (Exception ignore) {}
      finally
      {
        IOUtils.closeQuietly(is);
      }
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  public StoreManagerImpl(
    final FileBlobStoreWrap fileBlobStore,
    VolumeManager volumeManager
  )
  {
    this(
      fileBlobStore,
      volumeManager,
      new PrimaryStoreBuilder()
      {
        @Override
        public PrimaryStore build(FileBlobStoreWrap fileBlobStore, StoreVolume storeVolume)
        {
          return new FileBlobPrimaryStore(fileBlobStore, storeVolume);
        }
      }
    );
  }

  public StoreManagerImpl(
    final FileBlobStoreWrap fileBlobStore,
    VolumeManager volumeManager,
    PrimaryStoreBuilder primaryStoreBuilder
  )
  {
    mVolumeManager = volumeManager;
    mLock = new ReentrantLock();
    mCacheableStoreBuilderMap = new ConcurrentHashMap<>();
    mStoresCached = new ConcurrentHashMap<>();
    mFileBlobStore = fileBlobStore;
    mPrimaryStoreBuilder = primaryStoreBuilder;
  }

  @Override
  public void register(final CacheableStoreBuilder cacheableStoreBuilder, final String volumeId)
  {
    mLock.lock();
    try
    {
      mStoresCached.compute(volumeId,
        new BiFunction<String, Store, Store>()
        {
          @Override
          public Store apply(String vId, Store value)
          {
            if( mVolumeManager.isValidVolume(volumeId) )
            {
              mCacheableStoreBuilderMap.put(volumeId, cacheableStoreBuilder);
            }
            else
            {
              ZimbraLog.extensions.warn("Cannot register custom store for unknown volume " + volumeId);
            }
            return null;
          }
        }
      );
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
      mCacheableStoreBuilderMap.remove(volumeId);
      mStoresCached.remove(volumeId);
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
    for (Store store : mStoresCached.values())
    {
      store.startup();
    }
  }

  @Override
  public void shutdown()
  {
    for (Store store : mStoresCached.values())
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
  public Store getStore(final String volumeId)
  {
    if (!mCacheableStoreBuilderMap.containsKey(volumeId))
    {
      return mPrimaryStoreBuilder.build(
        mFileBlobStore,
        mVolumeManager.getById(volumeId)
      );
    }

    return mStoresCached.computeIfAbsent(
      volumeId, new Function<String, Store>() {
        @Override
        public Store apply(String s)
        {
          return mCacheableStoreBuilderMap.get(volumeId).make(volumeId);
        }
      }
    );
  }

  @Override
  public Store getStoreByName(String name)
  {
    return getStore(mVolumeManager.getVolumeByName(name).getId());
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

  @Override
  public void setPrimaryStoreBuilder(PrimaryStoreBuilder primaryStoreBuilder)
  {
    mPrimaryStoreBuilder = primaryStoreBuilder;
  }
}
