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

package org.openzal.zal;

import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;

import java.util.Objects;


public class CacheEntryType
{
  private final com.zimbra.soap.admin.type.CacheEntryType mCacheEntryType;

  public static CacheEntryType locale  = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.locale);
  public static CacheEntryType license = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.license);
  public static CacheEntryType account = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.account);
  public static CacheEntryType config  = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.config);
  public static CacheEntryType cos     = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.cos);
  public static CacheEntryType domain  = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.domain);
  public static CacheEntryType group   = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.group);
  public static CacheEntryType server  = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.server);

  public static CacheEntryType acl         = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.acl);
  public static CacheEntryType all         = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.all);
  public static CacheEntryType globalgrant = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.globalgrant);
  public static CacheEntryType mime        = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.mime);
  public static CacheEntryType galgroup    = new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.galgroup);

  CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType cacheEntryType)
  {
    mCacheEntryType = cacheEntryType;
  }

  com.zimbra.soap.admin.type.CacheEntryType getType()
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
      return new CacheEntryType(com.zimbra.soap.admin.type.CacheEntryType.fromString(s));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CacheEntryType that = (CacheEntryType) o;
    return mCacheEntryType == that.mCacheEntryType;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(mCacheEntryType);
  }
}
