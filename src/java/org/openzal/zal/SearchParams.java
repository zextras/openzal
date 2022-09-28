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


import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.mailbox.MailItem;
import javax.annotation.Nonnull;

import java.util.HashSet;
import java.util.Set;

public class SearchParams
{
  com.zimbra.cs.index.SearchParams mParams;

  public SearchParams()
  {
    mParams = new com.zimbra.cs.index.SearchParams();
    mParams.setTimeZone(null);
    mParams.setLocale(null);

    mParams.setSortBy(SortBy.DATE_DESC);
    mParams.setFetchMode(com.zimbra.cs.index.SearchParams.Fetch.NORMAL);
    mParams.setInDumpster(false);
  }

  public SearchParams(com.zimbra.cs.index.SearchParams params)
  {
    mParams = params;
    mParams.setSortBy(SortBy.DATE_DESC);
    mParams.setFetchMode(com.zimbra.cs.index.SearchParams.Fetch.NORMAL);
    mParams.setInDumpster(false);
  }

  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mParams);
  }

  public void setQueryString( String query )
  {
    mParams.setQueryString(query);
  }

  public void setTypes( byte[] types )
  {
    Set<MailItem.Type> set = new HashSet<MailItem.Type>();

    for( byte type : types )
    {
      set.add( Item.convertType(type) );
    }

    mParams.setTypes(set);
  }


  public void setChunkSize( int limit )
  {
    mParams.setChunkSize(limit);
  }

  public void setOffset( int offset )
  {
    mParams.setOffset(offset);
  }

  public void setPrefetch( boolean prefetch )
  {
    mParams.setPrefetch( prefetch );
  }
}
