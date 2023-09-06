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

import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.index.ZimbraQueryResults;
import javax.annotation.Nonnull;

import java.io.Closeable;
import java.io.IOException;

public class QueryResults implements Closeable
{
  @Nonnull private final ZimbraQueryResults mZimbraQueryResults;

  protected QueryResults(@Nonnull Object zimbraQueryResults)
  {
    if (zimbraQueryResults == null)
    {
      throw new NullPointerException();
    }
    mZimbraQueryResults = (ZimbraQueryResults) zimbraQueryResults;
  }

  public SearchHit getNext()
  {
    try
    {
      return new SearchHit(mZimbraQueryResults.getNext());
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean hasNext()
  {
    try
    {
      return mZimbraQueryResults.hasNext();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public SearchHit skipToHit(int int1)
  {
    try
    {
      return new SearchHit(mZimbraQueryResults.skipToHit(int1));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void close()
  {
    try
    {
      mZimbraQueryResults.close();
    }
    catch (IOException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
