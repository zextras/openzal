/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2015 ZeXtras S.r.l.
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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

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

  @Override
  public Object getOrDefault(Object key, Object defaultValue)
  {
    return mMap.getOrDefault(key, defaultValue);
  }

  @Override
  public void forEach(BiConsumer<? super String, ? super Object> action)
  {
    mMap.forEach(action);
  }

  @Override
  public void replaceAll(BiFunction<? super String, ? super Object, ? extends Object> function)
  {
    mMap.replaceAll(function);
  }

  @Override
  public Object putIfAbsent(String key, Object value)
  {
    return mMap.putIfAbsent(key, value);
  }

  @Override
  public boolean remove(Object key, Object value)
  {
    return mMap.remove(key, value);
  }

  @Override
  public boolean replace(String key, Object oldValue, Object newValue)
  {
    return mMap.replace(key, oldValue, newValue);
  }

  @Override
  public Object replace(String key, Object value)
  {
    return mMap.replace(key, value);
  }

  @Override
  public Object computeIfAbsent(String key, Function<? super String, ? extends Object> mappingFunction)
  {
    return mMap.computeIfAbsent(key, mappingFunction);
  }

  @Override
  public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ? extends Object> remappingFunction)
  {
    return mMap.computeIfPresent(key, remappingFunction);
  }

  @Override
  public Object compute(String key, BiFunction<? super String, ? super Object, ? extends Object> remappingFunction)
  {
    return mMap.compute(key, remappingFunction);
  }

  @Override
  public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction)
  {
    return mMap.merge(key, value, remappingFunction);
  }
}
