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

import javax.annotation.Nullable;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.MailServiceException;
import javax.annotation.Nonnull;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Metadata
{
  @Nonnull private final com.zimbra.cs.mailbox.Metadata mZObject;

  public Metadata()
  {
    this(new com.zimbra.cs.mailbox.Metadata());
  }

  public Metadata(@Nullable String encoded)
  {
    try
    {
      mZObject = new com.zimbra.cs.mailbox.Metadata(encoded);
    }
    catch (MailServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public Metadata(@Nullable Map<String, Object> map)
  {
    this();
    if( map != null )
    {
      for(String key : map.keySet())
      {
        put(key, map.get(key));
      }
    }
  }

  protected Metadata(@Nonnull Object metadata)
  {
    if (metadata == null)
    {
      throw new NullPointerException();
    }
    mZObject = (com.zimbra.cs.mailbox.Metadata) metadata;
  }

  public Metadata(TreeMap<String, String> map)
  {
    mZObject = new com.zimbra.cs.mailbox.Metadata(map);
  }

  public boolean containsKey(String key)
  {
    return mZObject.containsKey(key);
  }

  public long getLong(String key, long defaultValue)
  {
    try
    {
      return mZObject.getLong(key, defaultValue);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public Metadata put(String key, long value)
  {
    mZObject.put(key, value);
    return this;
  }

  public String toString()
  {
    return mZObject.toString();
  }

  public Map<String, Object> asMap()
  {
    return (Map<String, Object>) mZObject.asMap();
  }

  public String get(String key)
  {
    try
    {
      return mZObject.get(key);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String get(String key, String defaultValue)
  {
    return mZObject.get(key, defaultValue);
  }

  public Metadata remove(String key)
  {
    mZObject.remove(key);
    return this;
  }

  public Metadata put(String key, Object value)
  {
    if( value instanceof Metadata )
    {
      mZObject.put(key, ((Metadata)value).toZimbra(com.zimbra.cs.mailbox.Metadata.class));
      return this;
    }
    if( value instanceof MetadataList )
    {
      mZObject.put(key, ((MetadataList)value).toZimbra(com.zimbra.cs.mailbox.MetadataList.class));
      return this;
    }

    mZObject.put(key, value);
    return this;
  }

  public int getInt(String key, int i)
  {
    try
    {
      String value = mZObject.get(key);
      if( value == null )
      {
        return i;
      }
      else
      {
        return Integer.parseInt(value);
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public short getShort(String key, short i)
  {
    try
    {
      String value = mZObject.get(key);
      if( value == null )
      {
        return i;
      }
      else
      {
        return Short.valueOf(value);
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void put(String key, List<Object> list )
  {
    mZObject.put(key, list);
  }

  public MetadataList getList(String key)
  {
    try
    {
      Object obj = mZObject.getList(key, true);
      if( obj == null ) return new MetadataList();
      return new MetadataList(obj);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mZObject);
  }
}

