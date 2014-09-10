/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2014 ZeXtras S.r.l.
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

import org.openzal.zal.*;
import org.openzal.zal.MailboxManager;
import org.openzal.zal.lib.ZimbraDatabase;
import org.openzal.zal.log.ZimbraLog;
import com.zimbra.cs.extension.ExtensionUtil;
import com.zimbra.cs.extension.ZimbraExtension;

import java.lang.reflect.Field;

public class Zimbra
{
  private final Provisioning   mProvisioning;
  private final MailboxManager mMailboxManager;
  private final ZimbraDatabase mZimbraDatabase;
  private final StoreManager   mStoreManager;

  Zimbra()
  {
    try
    {
      mProvisioning = new Provisioning(com.zimbra.cs.account.Provisioning.getInstance());
      mMailboxManager = new MailboxManager(com.zimbra.cs.mailbox.MailboxManager.getInstance());
      mZimbraDatabase = new ZimbraDatabase();
      mStoreManager = new StoreManagerImp();
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public boolean isMailboxd()
  {
    Field sIsMailboxd;
    try
    {
      sIsMailboxd = com.zimbra.cs.util.Zimbra.class.getDeclaredField("sIsMailboxd");
      sIsMailboxd.setAccessible(true);
      return sIsMailboxd.getBoolean(null);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      return false;
    }
  }

  public Provisioning getProvisioning()
  {
    return mProvisioning;
  }

  public MailboxManager getMailboxManager()
  {
    return mMailboxManager;
  }

  public ZimbraDatabase getZimbraDatabase()
  {
    return mZimbraDatabase;
  }

  public StoreManager getStoreManager()
  {
    return mStoreManager;
  }

  public boolean shutdownExtension(String extensionName)
  {
    ZimbraExtension extension = ExtensionUtil.getExtension(extensionName);

    if( extension != null )
    {
      extension.destroy();
      return true;
    }

    return false;
  }
}
