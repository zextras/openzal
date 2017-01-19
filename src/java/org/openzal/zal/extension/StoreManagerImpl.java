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
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
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

  static
  {
    try
    {
      Field modifiersMethod = Method.class.getDeclaredField("modifiers");
      modifiersMethod.setAccessible(true);
      Method defineClassMethod = ClassLoader.class.getDeclaredMethod(
        "defineClass", byte[].class, int.class, int.class
      );
      defineClassMethod.setAccessible(true);
      modifiersMethod.setInt(
        defineClassMethod,
        (defineClassMethod.getModifiers() & (~Modifier.FINAL) & (~Modifier.PROTECTED)) | Modifier.PUBLIC
      );

      try
      {
        Class<?> parentClass = Class.forName("com.zimbra.cs.store.file.VolumeBlob");
        ClassLoader parentClassLoader = parentClass.getClassLoader();

        InputStream is = BootstrapClassLoader.class.getResourceAsStream("/com/zimbra/cs/store/file/VolumeBlobProxy");
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
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

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
      mStores.remove(volumeId);
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
