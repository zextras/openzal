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

package org.openzal.zal;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;


public class CacheEntryType
{
  /* $if MajorZimbraVersion >= 8 $ */
  private final com.zimbra.soap.admin.type.CacheEntryType mCacheEntryType;

  public static CacheEntryType locale  = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.locale);
  public static CacheEntryType skin    = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.skin);
  public static CacheEntryType license = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.license);
  public static CacheEntryType account = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.account);
  public static CacheEntryType config  = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.config);
  public static CacheEntryType cos     = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.cos);
  public static CacheEntryType domain  = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.domain);
  public static CacheEntryType group   = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.group);
  public static CacheEntryType server  = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.server);
  public static CacheEntryType zimlet  = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.zimlet);

  static CacheEntryType acl         = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.acl);
  static CacheEntryType uistrings   = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.uistrings);
  static CacheEntryType all         = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.all);
  static CacheEntryType globalgrant = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.globalgrant);
  static CacheEntryType mime        = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.mime);
  static CacheEntryType galgroup    = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.galgroup);

  CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType cacheEntryType)
  /* $else $
  private final com.zimbra.cs.account.Provisioning.CacheEntryType mCacheEntryType;

  public static CacheEntryType locale      = new CacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.locale);
  public static CacheEntryType skin        = new CacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.skin);
  public static CacheEntryType license     = new CacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.license);
  public static CacheEntryType account     = new CacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.account);
  public static CacheEntryType config      = new CacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.config);
  public static CacheEntryType cos         = new CacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.cos);
  public static CacheEntryType domain      = new CacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.domain);
  public static CacheEntryType group       = new CacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.group);
  public static CacheEntryType server      = new CacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.server);
  public static CacheEntryType zimlet      = new CacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.zimlet);

  CacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType cacheEntryType)
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

  public static CacheEntryType fromString(String s)
  {
    try
    {
  /* $if ZimbraVersion >= 8.0.0 $ */
      return new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.fromString(s));
  /* $else $
      return new CacheEntryType(com.zimbra.cs.account.Provisioning.CacheEntryType.fromString(s));
  /* $endif $ */

    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
