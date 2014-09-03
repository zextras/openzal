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

package org.openzal.zal;

import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
/* $if MajorZimbraVersion >= 8 $ */
import com.zimbra.soap.admin.type.CacheEntryType;
/* $else $
import com.zimbra.cs.account.Provisioning.CacheEntryType;
/* $endif $ */

public class ZECacheEntryType
{

  /* $if MajorZimbraVersion >= 8 $ */
  private final com.zimbra.soap.admin.type.CacheEntryType mCacheEntryType;

  static ZECacheEntryType acl         = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.acl);
  static ZECacheEntryType locale      = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.locale);
  static ZECacheEntryType skin        = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.skin);
  static ZECacheEntryType uistrings   = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.uistrings);
  static ZECacheEntryType license     = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.license);
  static ZECacheEntryType all         = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.all);
  static ZECacheEntryType account     = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.account);
  static ZECacheEntryType config      = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.config);
  static ZECacheEntryType globalgrant = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.globalgrant);
  static ZECacheEntryType cos         = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.cos);
  static ZECacheEntryType domain      = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.domain);
  static ZECacheEntryType galgroup    = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.galgroup);
  static ZECacheEntryType group       = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.group);
  static ZECacheEntryType mime        = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.mime);
  static ZECacheEntryType server      = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.server);
  static ZECacheEntryType zimlet      = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.zimlet);

  ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType cacheEntryType)
  /* $else $
  private final com.zimbra.cs.account.Provisioning.CacheEntryType mCacheEntryType;

  static ZECacheEntryType locale      = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.locale);
  static ZECacheEntryType skin        = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.skin);
  static ZECacheEntryType license     = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.license);
  static ZECacheEntryType account     = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.account);
  static ZECacheEntryType config      = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.config);
  static ZECacheEntryType cos         = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.cos);
  static ZECacheEntryType domain      = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.domain);
  static ZECacheEntryType group       = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.group);
  static ZECacheEntryType server      = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.server);
  static ZECacheEntryType zimlet      = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.zimlet);

  ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType cacheEntryType)
  /* $endif $ */
  {
    mCacheEntryType = cacheEntryType;
  }

  /* $if ZimbraVersion >= 8.0.0 $ */
  com.zimbra.soap.admin.type.CacheEntryType getType()
  /* $else $
  com.zimbra.cs.account.Provisioning.CacheEntryType getType()
  /* $endif $ */
  {
    return mCacheEntryType;
  }

  public String name()
  {
    return mCacheEntryType.name();
  }

  public static ZECacheEntryType fromString(String s)
  {
    try
    {
      return new ZECacheEntryType(CacheEntryType.fromString(s));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
