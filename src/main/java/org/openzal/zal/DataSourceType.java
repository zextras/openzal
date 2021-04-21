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
import com.zimbra.cs.account.AttributeClass;
import javax.annotation.Nonnull;


public class DataSourceType
{
  public static String gal  = "gal";
  public static String rss  = "rss";
  public static String imap = "imap";
  public static String pop3 = "pop3";

  public static String gal_OCName  = AttributeClass.galDataSource.getOCName();
  public static String rss_OCName  = AttributeClass.rssDataSource.getOCName();
  public static String imap_OCName = AttributeClass.imapDataSource.getOCName();
  public static String pop3_OCName = AttributeClass.pop3DataSource.getOCName();

  @Nonnull
  private final String mDataSourceType;

  protected DataSourceType(@Nonnull Object type)
  {
    if (type == null)
    {
      throw new NullPointerException();
    }

    mDataSourceType = ((com.zimbra.soap.admin.type.DataSourceType) type).name();
  }

  public static DataSourceType fromString(String dataSourceType)
  {
    try
    {
      return new DataSourceType(com.zimbra.soap.admin.type.DataSourceType.fromString(dataSourceType));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public String name()
  {
    return mDataSourceType;
  }

  @Override
  public int hashCode()
  {
    return mDataSourceType.hashCode();
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
      return cls.cast(com.zimbra.soap.admin.type.DataSourceType.fromString(mDataSourceType));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
