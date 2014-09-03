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

import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.NamedEntry;
import org.jetbrains.annotations.NotNull;

public class ZEEntry
{
  private final com.zimbra.cs.account.Entry mEntry;

  ZEEntry(@NotNull Object entry)
  {
    if ( entry == null )
    {
      throw new NullPointerException();
    }
    mEntry = (Entry)entry;
  }

  com.zimbra.cs.account.Entry toZimbra()
  {
    return mEntry;
  }

  public ZEEntryType getEntryType()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return new ZEEntryType(mEntry.getEntryType());
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public String getName()
  {
    return ((NamedEntry)mEntry).getName();
  }

  public String getAttr(String name, String defaultValue)
  {
    return mEntry.getAttr(name, defaultValue);
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    ZEEntry zeEntry = (ZEEntry) o;

    if (mEntry != null ? !mEntry.equals(zeEntry.mEntry) : zeEntry.mEntry != null)
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    return mEntry != null ? mEntry.hashCode() : 0;
  }

  public class ZEEntryType
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    ZEEntryType ENTRY                     = new ZEEntryType(Entry.EntryType.ENTRY);
    ZEEntryType ACCOUNT                   = new ZEEntryType(Entry.EntryType.ACCOUNT);
    ZEEntryType ALIAS                     = new ZEEntryType(Entry.EntryType.ALIAS);
    ZEEntryType CALRESOURCE               = new ZEEntryType(Entry.EntryType.CALRESOURCE);
    ZEEntryType COS                       = new ZEEntryType(Entry.EntryType.COS);
    ZEEntryType DATASOURCE                = new ZEEntryType(Entry.EntryType.DATASOURCE);
    ZEEntryType DISTRIBUTIONLIST          = new ZEEntryType(Entry.EntryType.DISTRIBUTIONLIST);
    ZEEntryType DOMAIN                    = new ZEEntryType(Entry.EntryType.DOMAIN);
    ZEEntryType DYNAMICGROUP              = new ZEEntryType(Entry.EntryType.DYNAMICGROUP);
    ZEEntryType DYNAMICGROUP_DYNAMIC_UNIT = new ZEEntryType(Entry.EntryType.DYNAMICGROUP_DYNAMIC_UNIT);
    ZEEntryType DYNAMICGROUP_STATIC_UNIT  = new ZEEntryType(Entry.EntryType.DYNAMICGROUP_STATIC_UNIT);
    ZEEntryType GLOBALCONFIG              = new ZEEntryType(Entry.EntryType.GLOBALCONFIG);
    ZEEntryType GLOBALGRANT               = new ZEEntryType(Entry.EntryType.GLOBALGRANT);
    ZEEntryType IDENTITY                  = new ZEEntryType(Entry.EntryType.IDENTITY);
    ZEEntryType MIMETYPE                  = new ZEEntryType(Entry.EntryType.MIMETYPE);
    ZEEntryType SERVER                    = new ZEEntryType(Entry.EntryType.SERVER);
    ZEEntryType UCSERVICE                 = new ZEEntryType(Entry.EntryType.UCSERVICE);
    ZEEntryType SIGNATURE                 = new ZEEntryType(Entry.EntryType.SIGNATURE);
    ZEEntryType XMPPCOMPONENT             = new ZEEntryType(Entry.EntryType.XMPPCOMPONENT);
    ZEEntryType ZIMLET                    = new ZEEntryType(Entry.EntryType.ZIMLET);

    private final Entry.EntryType mEntryType;

    private ZEEntryType(Entry.EntryType entryType)
    {
      mEntryType = entryType;
    }

    Entry.EntryType toZimbra()
    {
      return mEntryType;
    }

    public String getName()
    {
      return mEntryType.getName();
    }
    /* $else$
    public String getName()
    {
      throw new UnsupportedOperationException();
    }
    private ZEEntryType(){}
    /* $endif $ */
  }
}

