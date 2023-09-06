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

import com.zimbra.cs.extension.ExtensionUtil;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.store.file.FileBlobStore;
import java.lang.reflect.Field;
import java.util.Map;
import org.openzal.zal.FileBlobStoreWrapImpl;
import org.openzal.zal.MailboxManager;
import org.openzal.zal.MailboxManagerImp;
import org.openzal.zal.Provisioning;
import org.openzal.zal.ProvisioningImp;
import org.openzal.zal.StoreManager;
import org.openzal.zal.Utils;
import org.openzal.zal.VolumeManager;
import org.openzal.zal.lib.PermissiveMap;
import org.openzal.zal.lib.ZimbraDatabase;
import org.openzal.zal.log.ZimbraLog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openzal.zal.redolog.RedoLogProvider;

public class Zimbra
{
  @Nonnull private final Provisioning                     mProvisioning;
  @Nonnull private final MailboxManager                   mMailboxManager;
  @Nonnull private final ZimbraDatabase                   mZimbraDatabase;
           private       InternalOverrideStoreManager     mInternalOverrideStoreManager;
  @Nonnull private final VolumeManager                    mVolumeManager;
  @Nonnull private final com.zimbra.cs.store.StoreManager mZimbraStoreManager;
           private       StoreManager                     mStoreManager;
           private       boolean                          mCanOverrideStoreManager;

  public Zimbra()
  {
    this(null);
  }

public Zimbra(Zimbra zimbra)
  {
    try
    {
      mZimbraStoreManager = com.zimbra.cs.store.StoreManager.getInstance();
      mProvisioning = new ProvisioningImp(com.zimbra.cs.account.Provisioning.getInstance());
      mMailboxManager = new MailboxManagerImp(com.zimbra.cs.mailbox.MailboxManager.getInstance());
      mZimbraDatabase = new ZimbraDatabase();
      mVolumeManager = new VolumeManager();

      if (zimbra != null)
      {
        mStoreManager = new StoreManagerImpl(
                new FileBlobStoreWrapImpl((FileBlobStore) zimbra.mZimbraStoreManager),
                mVolumeManager
        );
        mCanOverrideStoreManager = true;
      }
      else
      {
        if (mZimbraStoreManager instanceof FileBlobStore)
        {
          mStoreManager = new StoreManagerImpl(
                  new FileBlobStoreWrapImpl((FileBlobStore) mZimbraStoreManager),
                  mVolumeManager
          );
          mCanOverrideStoreManager = true;
        }
        else
        {
          mStoreManager = null;
          mCanOverrideStoreManager = false;
        }
      }

      mInternalOverrideStoreManager = null;
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private static final Field sIsMailboxd;

  static
  {
    try
    {
      sIsMailboxd = com.zimbra.cs.util.Zimbra.class.getDeclaredField("sIsMailboxd");
      sIsMailboxd.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  public void forceMailboxd()
  {
    try
    {
      sIsMailboxd.set(null, true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  public boolean isMailboxd()
  {
    try
    {
      return sIsMailboxd.getBoolean(null);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  @Nonnull
  public Provisioning getProvisioning()
  {
    return mProvisioning;
  }

  @Nonnull
  public MailboxManager getMailboxManager()
  {
    return mMailboxManager;
  }

  @Nonnull
  public ZimbraDatabase getZimbraDatabase()
  {
    return mZimbraDatabase;
  }

  @Nullable
  public StoreManager getStoreManager()
  {
    return mStoreManager;
  }

  @Nonnull
  public VolumeManager getVolumeManager()
  {
    return mVolumeManager;
  }

  public boolean shutdownExtension(String extensionName)
  {
    ZimbraExtension extension = ExtensionUtil.getExtension(extensionName);

    if (extension != null)
    {
      extension.destroy();
      return true;
    }

    return false;
  }

  private static Field sInitializedExtensions;

  static
  {
    try
    {
      Class cls = com.zimbra.cs.extension.ExtensionUtil.class;
      sInitializedExtensions = cls.getDeclaredField("sInitializedExtensions");
      sInitializedExtensions.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  private static Field sStoreManagerInstance;

  static
  {
    try
    {
      sStoreManagerInstance = com.zimbra.cs.store.StoreManager.class.getDeclaredField("sInstance");
      sStoreManagerInstance.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  private static Field sRedoLogProviderInstance;

  static
  {
    try
    {
      sRedoLogProviderInstance = com.zimbra.cs.redolog.RedoLogProvider.class.getDeclaredField("theInstance");
      sRedoLogProviderInstance.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  public boolean removeExtension(String extensionName)
  {
    try
    {
      return ((Map) sInitializedExtensions.get(null)).remove(extensionName) != null;
    }
    catch (IllegalAccessException e)
    {
      throw new RuntimeException(e);
    }
  }

  public static void overrideExtensionMap()
  {
/*
  ZX-3303
  avoid concurrent modification exception when disabling an extension
  during extension postInit
*/

    try
    {
      Map map = (Map)sInitializedExtensions.get(null);
      sInitializedExtensions.set(
        null,
        new PermissiveMap<String,String>(map)
      );
    }
    catch (IllegalAccessException e)
    {
      throw new RuntimeException(e);
    }
  }

  public void overrideZimbraStoreManager()
  {
    overrideZimbraStoreManager(
      mStoreManager
    );
  }

  public void overrideZimbraRedoLogProvider(RedoLogProvider redoLogProvider)
  {
    ZimbraLog.extensions.info("ZAL override Zimbra RedoLog");
    try
    {
      sRedoLogProviderInstance.set(null, redoLogProvider);
    }
    catch( IllegalAccessException e )
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(e));
      throw new RuntimeException(e);
    }
  }

  public void overrideZimbraStoreManager(StoreManager storeManager)
  {
    if( !mCanOverrideStoreManager )
    {
      throw new UnsupportedOperationException(
        "Another ZAL extension already has already overridden Zimbra StoreManager");
    }
    mInternalOverrideStoreManager = new InternalOverrideStoreManager(storeManager, mVolumeManager);
    ZimbraLog.extensions.info("ZAL override Zimbra StoreManager");
    try
    {
      sStoreManagerInstance.set(null, mInternalOverrideStoreManager);
      mStoreManager = storeManager;
    }
    catch( IllegalAccessException e )
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(e));
      throw new RuntimeException(e);
    }
  }

  public void restoreZimbraStoreManager()
  {
    try
    {
      sStoreManagerInstance.set(null, mZimbraStoreManager);
    }
    catch (IllegalAccessException e)
    {
      throw new RuntimeException(e);
    }
  }
}
