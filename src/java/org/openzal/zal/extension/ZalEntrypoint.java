/*
 * ZAL - An abstraction layer for Zimbra.
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

import org.openzal.zal.ZalVersion;
import org.openzal.zal.lib.ZimbraVersion;
import com.zimbra.common.service.ServiceException;
/* $if ZimbraVersion >= 7.1.3 $ */
import com.zimbra.cs.extension.ExtensionException;
/* $endif $ */
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.extension.ZimbraExtensionPostInit;

import java.io.IOException;

public class ZalEntrypoint implements ZimbraExtension, ZimbraExtensionPostInit
{
  private final ExtensionManager mExtensionManager;

  public ZalEntrypoint()
  {
    mExtensionManager = new ExtensionManager();
  }

  @Override
  public String getName()
  {
    return "Zimbra Abstraction Layer";
  }

  @Override
  public void init()
    /* $if ZimbraVersion >= 7.1.3 $ */
    throws ExtensionException, ServiceException
    /* $else $
    throws ServiceException
    /* $endif $ */
  {
    if( !ZimbraVersion.current.equals(ZalVersion.target) )
    {
      /* $if ZimbraVersion >= 7.1.3 $ */
      throw new ExtensionException("Zimbra version mismatch - ZAL built for Zimbra: " +ZalVersion.target.toString());
      /* $else $
      throw new RuntimeException("Zimbra version mismatch - ZAL built for Zimbra: " +ZalVersion.target.toString());
      /* $endif $ */
    }

    try
    {
      mExtensionManager.loadExtensions();
    }
    catch (IOException e)
    {
      /* $if ZimbraVersion >= 7.1.3 $ */
      throw new ExtensionException("Unable to load extension", e);
      /* $else $
      throw new RuntimeException("Unable to load extension", e);
      /* $endif $ */
    }
  }

  @Override
  public void postInit()
  {
    mExtensionManager.startExtensions();
  }

  @Override
  public void destroy()
  {
    mExtensionManager.stopExtensions();
  }
}
