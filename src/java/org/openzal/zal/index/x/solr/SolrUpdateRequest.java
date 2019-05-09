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


import org.openzal.zal.index.x.IndexDocument;

import javax.annotation.Nonnull;

public class SolrUpdateRequest
{

  /* $if ZimbraX == 1 $
  private org.apache.solr.client.solrj.request.UpdateRequest mZObject;
  /* $endif $ */

  public SolrUpdateRequest()
  {
    /* $if ZimbraX == 1 $
    this(new org.apache.solr.client.solrj.request.UpdateRequest());

    /* $else $ */
    this(null);

    /* $endif $ */
  }

  public SolrUpdateRequest(@Nonnull Object zObject)
  {
    /* $if ZimbraX == 1 $
    {
      mZObject = (org.apache.solr.client.solrj.request.UpdateRequest) zObject;
    }
    /* $endif $ */
  }

  public SolrUpdateRequest add(IndexDocument document)
  {
    /* $if ZimbraX == 1 $
    {
      mZObject.add(document.toZimbra(com.zimbra.cs.index.IndexDocument.class).toInputDocument());
      return this;
    }
     /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public SolrUpdateRequest deleteByQuery(String query)
  {
    /* $if ZimbraX == 1 $
    {
      mZObject.deleteByQuery(query);
      return this;
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public String toString()
  {
    /* $if ZimbraX == 1 $
    {
      return mZObject.toString();
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraX == 1 $
    {
      return target.cast(mZObject);
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }
}
