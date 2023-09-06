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
import com.zimbra.cs.index.QueryInfo;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.index.ZimbraHit;
import com.zimbra.cs.index.ZimbraQueryResults;
import javax.annotation.Nonnull;

import java.io.IOException;
import java.util.List;

public class FakeQueryResults extends QueryResults
{
  private final List<SearchHit> mZimbraQueryResults;
  private int element;

  public FakeQueryResults(@Nonnull List<SearchHit> zimbraQueryResults)
  {
    super(
      new ZimbraQueryResults(){

        @Override
        public void resetIterator() throws ServiceException
        {

        }

        @Override
        public ZimbraHit getNext() throws ServiceException
        {
          return null;
        }

        @Override
        public ZimbraHit peekNext() throws ServiceException
        {
          return null;
        }

        @Override
        public ZimbraHit skipToHit(int hitNo) throws ServiceException
        {
          return null;
        }

        @Override
        public boolean hasNext() throws ServiceException
        {
          return false;
        }

        @Override
        public SortBy getSortBy()
        {
          return null;
        }

        @Override
        public List<QueryInfo> getResultInfo()
        {
          return null;
        }

        @Override
        public long getCursorOffset()
        {
          return 0;
        }

        @Override
        public void close() throws IOException
        {

        }

        @Override
        public boolean isPreSorted()
        {
          return false;
        }

      }
    );
    mZimbraQueryResults = zimbraQueryResults;
    element = 0;
  }

  @Override
  public SearchHit getNext()
  {
    return mZimbraQueryResults.get(element++);
  }

  @Override
  public boolean hasNext()
  {
    return element < mZimbraQueryResults.size();
  }

  @Override
  public SearchHit skipToHit(int int1)
  {
    return mZimbraQueryResults.get(0);
  }

  @Override
  public void close()
  {}
}
