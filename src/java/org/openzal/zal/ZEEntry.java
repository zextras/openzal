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

  protected com.zimbra.cs.account.Entry getProxiedEntry()
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
    public ZEEntryType ENTRY                     = new ZEEntryType(Entry.EntryType.ENTRY);
    public ZEEntryType ACCOUNT                   = new ZEEntryType(Entry.EntryType.ACCOUNT);
    public ZEEntryType ALIAS                     = new ZEEntryType(Entry.EntryType.ALIAS);
    public ZEEntryType CALRESOURCE               = new ZEEntryType(Entry.EntryType.CALRESOURCE);
    public ZEEntryType COS                       = new ZEEntryType(Entry.EntryType.COS);
    public ZEEntryType DATASOURCE                = new ZEEntryType(Entry.EntryType.DATASOURCE);
    public ZEEntryType DISTRIBUTIONLIST          = new ZEEntryType(Entry.EntryType.DISTRIBUTIONLIST);
    public ZEEntryType DOMAIN                    = new ZEEntryType(Entry.EntryType.DOMAIN);
    public ZEEntryType DYNAMICGROUP              = new ZEEntryType(Entry.EntryType.DYNAMICGROUP);
    public ZEEntryType DYNAMICGROUP_DYNAMIC_UNIT = new ZEEntryType(Entry.EntryType.DYNAMICGROUP_DYNAMIC_UNIT);
    public ZEEntryType DYNAMICGROUP_STATIC_UNIT  = new ZEEntryType(Entry.EntryType.DYNAMICGROUP_STATIC_UNIT);
    public ZEEntryType GLOBALCONFIG              = new ZEEntryType(Entry.EntryType.GLOBALCONFIG);
    public ZEEntryType GLOBALGRANT               = new ZEEntryType(Entry.EntryType.GLOBALGRANT);
    public ZEEntryType IDENTITY                  = new ZEEntryType(Entry.EntryType.IDENTITY);
    public ZEEntryType MIMETYPE                  = new ZEEntryType(Entry.EntryType.MIMETYPE);
    public ZEEntryType SERVER                    = new ZEEntryType(Entry.EntryType.SERVER);
    public ZEEntryType UCSERVICE                 = new ZEEntryType(Entry.EntryType.UCSERVICE);
    public ZEEntryType SIGNATURE                 = new ZEEntryType(Entry.EntryType.SIGNATURE);
    public ZEEntryType XMPPCOMPONENT             = new ZEEntryType(Entry.EntryType.XMPPCOMPONENT);
    public ZEEntryType ZIMLET                    = new ZEEntryType(Entry.EntryType.ZIMLET);

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
    /* $endif $ */
  }
}

