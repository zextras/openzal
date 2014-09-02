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

import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.index.ZimbraQueryResults;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ZEQueryResults
{
  private final ZimbraQueryResults mZimbraQueryResults;

  protected ZEQueryResults(@NotNull Object zimbraQueryResults)
  {
    if ( zimbraQueryResults == null )
    {
      throw new NullPointerException();
    }
    mZimbraQueryResults = (ZimbraQueryResults)zimbraQueryResults;
  }

  public ZEHit getNext()
  {
    try
    {
      return new ZEHit(mZimbraQueryResults.getNext());
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

  public ZEHit skipToHit(int int1)
  {
    try
    {
      return new ZEHit(mZimbraQueryResults.skipToHit(int1));
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
      /* $if MajorZimbraVersion >= 8 $ */
      mZimbraQueryResults.close();
      /* $else$
      mZimbraQueryResults.doneWithSearchResults();
      /* $endif $ */
    }
    /* $if MajorZimbraVersion >= 8 $ */
    catch (IOException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else$
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $endif$ */
  }
}
