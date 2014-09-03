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

  public static ZECacheEntryType locale      = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.locale);
  public static ZECacheEntryType skin        = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.skin);
  public static ZECacheEntryType license     = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.license);
  public static ZECacheEntryType account     = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.account);
  public static ZECacheEntryType config      = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.config);
  public static ZECacheEntryType cos         = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.cos);
  public static ZECacheEntryType domain      = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.domain);
  public static ZECacheEntryType group       = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.group);
  public static ZECacheEntryType server      = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.server);
  public static ZECacheEntryType zimlet      = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.zimlet);

  static ZECacheEntryType acl         = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.acl);
  static ZECacheEntryType uistrings   = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.uistrings);
  static ZECacheEntryType all         = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.all);
  static ZECacheEntryType globalgrant = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.globalgrant);
  static ZECacheEntryType mime        = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.mime);
  static ZECacheEntryType galgroup    = new ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.galgroup);

  ZECacheEntryType(com.zimbra.soap.admin.type.CacheEntryType cacheEntryType)
  /* $else $
  private final com.zimbra.cs.account.Provisioning.CacheEntryType mCacheEntryType;

  public static ZECacheEntryType locale      = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.locale);
  public static ZECacheEntryType skin        = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.skin);
  public static ZECacheEntryType license     = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.license);
  public static ZECacheEntryType account     = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.account);
  public static ZECacheEntryType config      = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.config);
  public static ZECacheEntryType cos         = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.cos);
  public static ZECacheEntryType domain      = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.domain);
  public static ZECacheEntryType group       = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.group);
  public static ZECacheEntryType server      = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.server);
  public static ZECacheEntryType zimlet      = new ZECacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.zimlet);

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
