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

package org.openzal.zal.index;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

class IndexerProxyMap implements Map<String, Object>
{
  private final Map<String, Object> mMap;
  private final MimeHandlerProvider mMimeHandlerProvider;

  IndexerProxyMap(
    Map<String, Object> map,
    MimeHandlerProvider mimeHandlerProvider
  )
  {
    mMap = map;
    mMimeHandlerProvider = mimeHandlerProvider;
  }

  @Override
  public Object get(Object key)
  {
    String strKey = (String) key;

    String contentType = "";
    String fileExtension = "";
    String data[] = strKey.split(",");

    if (data.length > 0)
    {
      contentType = data[0];
    }
    if (data.length > 1)
    {
      fileExtension = data[1];
    }

    Object obj = mMimeHandlerProvider.getMimeHandlerFor(
      contentType, fileExtension
    );

    if (obj != null)
    {
      return obj;
    }
    else
    {
      return mMap.get(key);
    }
  }

  @Override
  public int size()
  {
    return mMap.size();
  }

  @Override
  public boolean isEmpty()
  {
    return mMap.isEmpty();
  }

  @Override
  public boolean containsKey(Object key)
  {
    return mMap.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value)
  {
    return mMap.containsValue(value);
  }

  @Override
  public Object put(String key, Object value)
  {
    return mMap.put(key, value);
  }

  @Override
  public Object remove(Object key)
  {
    return mMap.remove(key);
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> m)
  {
    mMap.putAll(m);
  }

  @Override
  public void clear()
  {
    mMap.clear();
  }

  @Override
  public Set<String> keySet()
  {
    return mMap.keySet();
  }

  @Override
  public Collection<Object> values()
  {
    return mMap.values();
  }

  @Override
  public Set<Entry<String, Object>> entrySet()
  {
    return mMap.entrySet();
  }

  @Override
  public boolean equals(Object o)
  {
    return mMap.equals(o);
  }

  @Override
  public int hashCode()
  {
    return mMap.hashCode();
  }
}
