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

package org.openzal.zal.lib;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
  This map allow object removing during iteration, doesn't throw ConcurrentModificationException
  This map is NOT optimized for any usage, avoid using it unless necessary.
  This map is NOT thread-safe.
*/
public class PermissiveMap<K, V> extends AbstractMap<K, V> implements Map<K, V>
{
  private final List<K> mKeys;
  private final List<V> mValues;

  public PermissiveMap()
  {
    mKeys = new ArrayList<K>(16);
    mValues = new ArrayList<V>(16);
  }

  public PermissiveMap(Map<K,V> map)
  {
    this();
    putAll(map);
  }

  @Override
  public int size()
  {
    return mKeys.size();
  }

  @Override
  public boolean isEmpty()
  {
    return mKeys.isEmpty();
  }

  @Override
  public boolean containsKey(Object key)
  {
    return mKeys.contains(key);
  }

  @Override
  public boolean containsValue(Object value)
  {
    return mValues.contains(value);
  }

  @Override
  public V get(Object key)
  {
    int idx = mKeys.indexOf(key);
    return idx == -1 ? null : mValues.get(idx);
  }

  @Override
  public V put(K key, V value)
  {
    V old = null;
    int idx = mKeys.indexOf(key);
    if (idx != -1)
    {
      mKeys.remove(idx);
      old = mValues.remove(idx);
    }
    mKeys.add(key);
    mValues.add(value);
    return old;
  }

  @Override
  public V remove(Object key)
  {
    int idx = mKeys.indexOf(key);
    if (idx != -1)
    {
      mKeys.remove(idx);
      return mValues.remove(idx);
    }
    return null;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m)
  {
    for (Map.Entry<? extends K, ? extends V> entry : m.entrySet())
    {
      put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void clear()
  {
    mKeys.clear();
    mValues.clear();
  }

  @Override
  public Set<K> keySet()
  {
    return new PermissiveSet(mKeys);
  }

  @Override
  public Collection<V> values()
  {
    return new PermissiveSet(mValues);
  }

  class IndexedEntry implements Entry<K, V>
  {
    private final int mIdx;
    private final K   mKey;
    private final V   mValue;

    IndexedEntry(int idx, K key, V value)
    {
      mIdx = idx;
      mKey = key;
      mValue = value;
    }

    @Override
    public K getKey()
    {
      return mKey;
    }

    @Override
    public V getValue()
    {
      return mValue;
    }

    @Override
    public V setValue(V value)
    {
      V old = mValues.remove(mIdx);
      mValues.add(mIdx, value);
      return old;
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;

      Entry that = (Entry) o;

      if (mKey != null ? !mKey.equals(that.getKey()) : that.getKey() != null)
        return false;
      return !(mValue != null ? !mValue.equals(that.getValue()) : that.getValue() != null);
    }

    public int hashCode()
    {
      return (mKey == null ? 0 : mKey.hashCode()) ^ (mValue == null ? 0 : mValue.hashCode());
    }
  }

  static class SimpleEntry<K, V> implements Entry<K, V>
  {
    private final K mKey;
    private       V mValue;

    SimpleEntry(K key, V value)
    {
      mKey = key;
      mValue = value;
    }

    @Override
    public K getKey()
    {
      return mKey;
    }

    @Override
    public V getValue()
    {
      return mValue;
    }

    @Override
    public V setValue(V value)
    {
      V old = mValue;
      mValue = value;
      return old;
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;

      Entry that = (Entry) o;

      if (mKey != null ? !mKey.equals(that.getKey()) : that.getKey() != null)
        return false;
      return !(mValue != null ? !mValue.equals(that.getValue()) : that.getValue() != null);
    }

    public int hashCode()
    {
      return (mKey == null ? 0 : mKey.hashCode()) ^ (mValue == null ? 0 : mValue.hashCode());
    }
  }

  class EntrySet extends AbstractSet<Entry<K, V>> implements Set<Entry<K, V>>
  {
    @Override
    public int size()
    {
      return mKeys.size();
    }

    @Override
    public boolean isEmpty()
    {
      return mKeys.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
      if (o instanceof Entry)
      {
        Entry<K, V> entry = (Entry<K, V>) o;
        int idx = mKeys.indexOf(entry.getKey());
        if (idx != -1)
        {
          V value = mValues.get(idx);
          return (value == null && entry.getValue() == null) ||
            ((value != null && entry.getValue() != null) && entry.getValue().equals(value));
        }
      }
      return false;
    }

    @Override
    public Iterator<Entry<K, V>> iterator()
    {
      return new Iterator<Entry<K, V>>()
      {
        int mIndex = 0;

        @Override
        public boolean hasNext()
        {
          return mIndex < mKeys.size();
        }

        @Override
        public Entry<K, V> next()
        {
          IndexedEntry entry = new IndexedEntry(
            mIndex,
            mKeys.get(mIndex),
            mValues.get(mIndex)
          );
          mIndex++;
          return entry;
        }

        @Override
        public void remove()
        {
          if (mIndex > 0)
          {
            --mIndex;
            mKeys.remove(mIndex);
            mValues.remove(mIndex);
          }
          else
          {
            throw new IllegalStateException();
          }
        }
      };
    }

    @Override
    public boolean add(Entry<K, V> kvEntry)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o)
    {
      Entry<K, V> entry = (Entry<K, V>) o;
      int index = mKeys.indexOf(entry.getKey());
      if (index != -1)
      {
        V value = mValues.get(index);
        V other = entry.getValue();
        if( value == other || (value != null && other != null && value.equals(other)))
        {
          mKeys.remove(index);
          mValues.remove(index);
          return true;
        }
      }
      return false;
    }

    @Override
    public void clear()
    {
      PermissiveMap.this.clear();
    }
  }

  @Override
  public Set<Entry<K, V>> entrySet()
  {
    return new EntrySet();
  }

  class PermissiveSet<X> extends AbstractSet<X> implements Set<X>
  {
    private final List<X> mList;

    public PermissiveSet(List<X> list)
    {
      mList = list;
    }

    @Override
    public int size()
    {
      return mList.size();
    }

    @Override
    public boolean isEmpty()
    {
      return mList.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
      return mList.contains(o);
    }

    @Override
    public Iterator<X> iterator()
    {
      return new Iterator<X>()
      {
        int mIndex = 0;

        @Override
        public void remove()
        {
          if (mIndex > 0)
          {
            mIndex--;
            mValues.remove(mIndex);
            mKeys.remove(mIndex);
          }
          else
          {
            throw new IllegalStateException();
          }
        }

        @Override
        public boolean hasNext()
        {
          return mIndex < mList.size();
        }

        @Override
        public X next()
        {
          return mList.get(mIndex++);
        }
      };
    }

    @Override
    public Object[] toArray()
    {
      return mList.toArray(new Object[size()]);
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
      return mList.toArray(a);
    }

    @Override
    public boolean remove(Object o)
    {
      int index = mList.indexOf(o);
      if (index != -1)
      {
        mValues.remove(index);
        mKeys.remove(index);
        return true;
      }
      return false;
    }

    @Override
    public void clear()
    {
      PermissiveMap.this.clear();
    }
  }
}
