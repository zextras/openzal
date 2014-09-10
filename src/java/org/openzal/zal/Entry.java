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

import com.zimbra.cs.account.NamedEntry;
import org.jetbrains.annotations.NotNull;

public class Entry
{
  private final com.zimbra.cs.account.Entry mEntry;

  Entry(@NotNull Object entry)
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
    return ((NamedEntry) mEntry).getName();
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

    Entry entry = (Entry) o;

    if (mEntry != null ? !mEntry.equals(entry.mEntry) : entry.mEntry != null)
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
    ZEEntryType ENTRY                     = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.ENTRY);
    ZEEntryType ACCOUNT                   = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.ACCOUNT);
    ZEEntryType ALIAS                     = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.ALIAS);
    ZEEntryType CALRESOURCE               = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.CALRESOURCE);
    ZEEntryType COS                       = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.COS);
    ZEEntryType DATASOURCE                = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.DATASOURCE);
    ZEEntryType DISTRIBUTIONLIST          = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.DISTRIBUTIONLIST);
    ZEEntryType DOMAIN                    = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.DOMAIN);
    ZEEntryType DYNAMICGROUP              = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.DYNAMICGROUP);
    ZEEntryType DYNAMICGROUP_DYNAMIC_UNIT = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.DYNAMICGROUP_DYNAMIC_UNIT);
    ZEEntryType DYNAMICGROUP_STATIC_UNIT  = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.DYNAMICGROUP_STATIC_UNIT);
    ZEEntryType GLOBALCONFIG              = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.GLOBALCONFIG);
    ZEEntryType GLOBALGRANT               = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.GLOBALGRANT);
    ZEEntryType IDENTITY                  = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.IDENTITY);
    ZEEntryType MIMETYPE                  = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.MIMETYPE);
    ZEEntryType SERVER                    = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.SERVER);
    ZEEntryType UCSERVICE                 = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.UCSERVICE);
    ZEEntryType SIGNATURE                 = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.SIGNATURE);
    ZEEntryType XMPPCOMPONENT             = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.XMPPCOMPONENT);
    ZEEntryType ZIMLET                    = new ZEEntryType(com.zimbra.cs.account.Entry.EntryType.ZIMLET);

    private final com.zimbra.cs.account.Entry.EntryType mEntryType;

    private ZEEntryType(com.zimbra.cs.account.Entry.EntryType entryType)
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
    /* $else$
    public String getName()
    {
      throw new UnsupportedOperationException();
    }
    private ZEEntryType(){}
    /* $endif $ */
  }
}

