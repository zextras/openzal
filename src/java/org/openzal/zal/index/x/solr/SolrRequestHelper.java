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

package org.openzal.zal.index.x.solr;

import org.openzal.zal.exceptions.ZimbraException;

import javax.annotation.Nonnull;

/* $if ZimbraX == 1 $
import com.zimbra.common.service.ServiceException;
import org.openzal.zal.exceptions.ExceptionWrapper;
/* $endif $ */

public class SolrRequestHelper
{

  /* $if ZimbraX == 1 $
  private com.zimbra.cs.index.solr.SolrRequestHelper mZObject;
  /* $endif $ */

  public SolrRequestHelper(@Nonnull Object zObject)
  {
    /* $if ZimbraX == 1 $
    {
      mZObject = (com.zimbra.cs.index.solr.SolrRequestHelper) zObject;
    }
    /* $endif $ */
  }

  public void executeUpdate(String accountId, SolrUpdateRequest request)
    throws ZimbraException
  {
    /* $if ZimbraX == 1 $
    {
      try
      {
        mZObject.executeUpdate(accountId, request.toZimbra(org.apache.solr.client.solrj.request.UpdateRequest.class));
      }
      catch( ServiceException e )
      {
        throw ExceptionWrapper.wrap(e);
      }
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public String getId(String accountId, Object... idParts)
  {
     /* $if ZimbraX == 1 $
    {
      return mZObject.getSolrId(accountId, idParts);
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public boolean needsAccountFilter()
  {
     /* $if ZimbraX == 1 $
    {
      return mZObject.needsAccountFilter();
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }
}
