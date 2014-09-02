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
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Metadata;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

public class ZEMetadata
{
  private final Metadata mMetadata;

  protected ZEMetadata(@NotNull Object metadata)
  {
    if ( metadata == null )
    {
      throw new NullPointerException();
    }
    mMetadata = (Metadata)metadata;
  }

  public ZEMetadata(String encoded)
  {
    try
    {
      mMetadata = new Metadata(encoded);
    }
    catch (MailServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $if ZimbraVersion < 8.0.0 $
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $endif $ */
  }

  public ZEMetadata()
  {
    mMetadata = new Metadata();
  }

  public ZEMetadata(TreeMap<String, String> map)
  {
    mMetadata = new Metadata(map);
  }

  public boolean containsKey(String key)
  {
    return mMetadata.containsKey(key);
  }

  public long getLong(String key, long defaultValue)
  {
    try
    {
      return mMetadata.getLong(key, defaultValue);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZEMetadata put(String key, long value)
  {
    mMetadata.put(key, value);
    return this;
  }

  public String toString()
  {
    return mMetadata.toString();
  }

  public Map<String, Object> asMap()
  {
    return (Map<String, Object>) mMetadata.asMap();
  }

  public String get(String key)
  {
    try
    {
      return mMetadata.get(key);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String get(String key, String defaultValue)
  {
    return mMetadata.get(key, defaultValue);
  }

  public ZEMetadata remove(String key)
  {
    mMetadata.remove(key);
    return this;
  }

  public ZEMetadata put(String key, Object value)
  {
    mMetadata.put(key, value);
    return this;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mMetadata);
  }
}

