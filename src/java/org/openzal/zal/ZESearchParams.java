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


import com.zimbra.cs.index.SearchParams;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.mailbox.MailItem;

import java.util.HashSet;
import java.util.Set;

public class ZESearchParams
{
  SearchParams mParams;

  public ZESearchParams()
  {
    mParams = new SearchParams();
    mParams.setTimeZone(null);
    mParams.setLocale(null);

/* $if MajorZimbraVersion >= 8 $ */
    mParams.setSortBy(SortBy.DATE_DESC);
    mParams.setFetchMode(SearchParams.Fetch.NORMAL);
    mParams.setInDumpster(false);
/* $endif$ */
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mParams);
  }

  public void setQueryString( String query )
  {
/* $if MajorZimbraVersion >= 8 $ */
    mParams.setQueryString(query);
/* $else$
    mParams.setQueryStr(query);
 $endif$ */
  }

  public void setTypes( byte[] types )
  {
/* $if MajorZimbraVersion >= 8 $ */
    Set<MailItem.Type> set = new HashSet<MailItem.Type>();

    for( byte type : types )
    {
      set.add( ZEItem.convertType(type) );
    }

    mParams.setTypes(set);

/* $else$
    mParams.setTypes(types);
 $endif$ */
  }


  public void setChunkSize( int limit )
  {
    mParams.setChunkSize(limit);
  }

  public void setPrefetch( boolean prefetch )
  {
    mParams.setPrefetch( prefetch );
  }
}
