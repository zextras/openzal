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

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.NamedEntry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openzal.zal.exceptions.ExceptionWrapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Entry
{
  private final com.zimbra.cs.account.Entry mEntry;

  Entry(@Nonnull Object entry)
  {
    if (entry == null)
    {
      throw new NullPointerException();
    }
    mEntry = (com.zimbra.cs.account.Entry) entry;
  }

  com.zimbra.cs.account.Entry toZimbra()
  {
    return mEntry;
  }

  public EntryType getEntryType()
  {
    return new EntryType(mEntry.getEntryType());
  }

  public String getName()
  {
    return ((NamedEntry) mEntry).getName();
  }

  public String getAttr(String name, String defaultValue)
  {
    return mEntry.getAttr(name, defaultValue);
  }

  @Override
  public boolean equals(@Nullable Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    Entry entry = (Entry) o;

    if (mEntry != null ? !mEntry.equals(entry.mEntry) : entry.mEntry != null)
    {
      return false;
    }

    return true;
  }

  @Nonnull
  public Set<String> getMultiAttrSet(String name)
  {
    return new HashSet<String>(mEntry.getMultiAttrSet(name));
  }

  public void addMultiAttrValue(String key, String value)
  {
    if (value != null && !value.isEmpty())
    {
      Set<String> values = getMultiAttrSet(key);
      values.add(value);

      modify(
        Collections.<String, Object>singletonMap(
          key,
          values.toArray(new String[values.size()])
        )
      );
    }
  }

  public void removeMultiAttrValue(String key, String value)
  {
    if (value != null && !value.isEmpty())
    {
      Set<String> values = getMultiAttrSet(key);
      values.remove(value);

      modify(
        Collections.<String, Object>singletonMap(
          key,
          values.toArray(new String[values.size()])
        )
      );
    }
  }

  public void modify(Map<String, Object> attrs)
  {
    modify(attrs, true);
  }
  public void modify(Map<String, Object> attrs, boolean checkImmutable)
  {
    try
    {
      mEntry.getProvisioning().modifyAttrs(mEntry, attrs, checkImmutable);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public class EntryType
  {
    EntryType ENTRY                     = new EntryType(com.zimbra.cs.account.Entry.EntryType.ENTRY);
    EntryType ACCOUNT                   = new EntryType(com.zimbra.cs.account.Entry.EntryType.ACCOUNT);
    EntryType ALIAS                     = new EntryType(com.zimbra.cs.account.Entry.EntryType.ALIAS);
    EntryType CALRESOURCE               = new EntryType(com.zimbra.cs.account.Entry.EntryType.CALRESOURCE);
    EntryType COS                       = new EntryType(com.zimbra.cs.account.Entry.EntryType.COS);
    EntryType DATASOURCE                = new EntryType(com.zimbra.cs.account.Entry.EntryType.DATASOURCE);
    EntryType DISTRIBUTIONLIST          = new EntryType(com.zimbra.cs.account.Entry.EntryType.DISTRIBUTIONLIST);
    EntryType DOMAIN                    = new EntryType(com.zimbra.cs.account.Entry.EntryType.DOMAIN);
    EntryType DYNAMICGROUP              = new EntryType(com.zimbra.cs.account.Entry.EntryType.DYNAMICGROUP);
    EntryType DYNAMICGROUP_DYNAMIC_UNIT = new EntryType(com.zimbra.cs.account.Entry.EntryType.DYNAMICGROUP_DYNAMIC_UNIT);
    EntryType DYNAMICGROUP_STATIC_UNIT  = new EntryType(com.zimbra.cs.account.Entry.EntryType.DYNAMICGROUP_STATIC_UNIT);
    EntryType GLOBALCONFIG              = new EntryType(com.zimbra.cs.account.Entry.EntryType.GLOBALCONFIG);
    EntryType GLOBALGRANT               = new EntryType(com.zimbra.cs.account.Entry.EntryType.GLOBALGRANT);
    EntryType IDENTITY                  = new EntryType(com.zimbra.cs.account.Entry.EntryType.IDENTITY);
    EntryType MIMETYPE                  = new EntryType(com.zimbra.cs.account.Entry.EntryType.MIMETYPE);
    EntryType SERVER                    = new EntryType(com.zimbra.cs.account.Entry.EntryType.SERVER);
    EntryType SIGNATURE                 = new EntryType(com.zimbra.cs.account.Entry.EntryType.SIGNATURE);
    EntryType XMPPCOMPONENT             = new EntryType(com.zimbra.cs.account.Entry.EntryType.XMPPCOMPONENT);
    private final com.zimbra.cs.account.Entry.EntryType mEntryType;

    private EntryType(com.zimbra.cs.account.Entry.EntryType entryType)
    {
      mEntryType = entryType;
    }

    com.zimbra.cs.account.Entry.EntryType toZimbra()
    {
      return mEntryType;
    }

    public String getName()
    {
      return mEntryType.getName();
    }
  }

  public Boolean getBooleanAttr(String name, Boolean defaultValue) {
    return mEntry.getBooleanAttr(name, defaultValue);
  }

}

