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
import com.zimbra.cs.account.AttributeClass;
import org.jetbrains.annotations.NotNull;
/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.soap.admin.type.DataSourceType;
/* $else $
import com.zimbra.cs.account.DataSource;
/* $endif $ */

public class ZEDataSourceType
{
  public static String gal  = "gal";
  public static String rss  = "rss";
  public static String imap = "imap";
  public static String pop3 = "pop3";

  public static String gal_OCName  = AttributeClass.galDataSource.getOCName();
  public static String rss_OCName  = AttributeClass.rssDataSource.getOCName();
  public static String imap_OCName = AttributeClass.imapDataSource.getOCName();
  public static String pop3_OCName = AttributeClass.pop3DataSource.getOCName();

  private final String mDataSourceType;

  protected ZEDataSourceType(@NotNull Object type)
  {
    if ( type == null )
    {
      throw new NullPointerException();
    }

    /* $if ZimbraVersion >= 8.0.0 $ */
    mDataSourceType = ((DataSourceType)type).name();
    /* $else $
    mDataSourceType = ((DataSource.Type)type).name();
    /* $endif $ */
  }

  public static ZEDataSourceType fromString(String dataSourceType)
  {
    try
    {
      /* $if ZimbraVersion >= 8.0.0 $ */
      return new ZEDataSourceType(DataSourceType.fromString(dataSourceType));
      /* $else $
      return new ZEDataSourceType(DataSource.Type.fromString(dataSourceType));
      /* $endif $ */
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
  public String name()
  {
    return mDataSourceType;
  }

  @Override
  public int hashCode()
  {
    return mDataSourceType != null ? mDataSourceType.hashCode() : 0;
  }

  @Override
  public boolean equals(Object other)
  {
    return mDataSourceType.equals(other);
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    try
    {
      /* $if ZimbraVersion >= 8.0.0 $ */
      return cls.cast(DataSourceType.fromString(mDataSourceType));
      /* $else $
      return cls.cast(DataSource.Type.fromString(mDataSourceType));
      /* $endif $ */
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
