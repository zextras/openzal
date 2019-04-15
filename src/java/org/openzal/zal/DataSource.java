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
import javax.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;

public class DataSource
{
  @Nonnull private final com.zimbra.cs.account.DataSource mDataSource;

  DataSource(@Nonnull Object dataSource)
  {
    if (dataSource == null)
    {
      throw new NullPointerException();
    }
    mDataSource = (com.zimbra.cs.account.DataSource) dataSource;
  }

  public static String decryptData(String dataSourceId, String data)
  {
    try
    {
      return com.zimbra.cs.account.DataSource.decryptData(dataSourceId, data);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public int getFolderId()
  {
    return mDataSource.getFolderId();
  }

  public static String encryptData(String dataSourceId, String data)
  {
    try
    {
      return com.zimbra.cs.account.DataSource.encryptData(dataSourceId, data);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String getId()
  {
    return mDataSource.getId();
  }

  public Map<String, Object> getAttrs(boolean applyDefaults)
  {
    return new HashMap<String, Object>(mDataSource.getAttrs(applyDefaults));
  }

  com.zimbra.cs.account.DataSource toZimbra()
  {
    return mDataSource;
  }
}

