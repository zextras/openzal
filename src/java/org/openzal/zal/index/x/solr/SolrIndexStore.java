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


import java.io.IOException;
import org.openzal.zal.MailboxIndex;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.index.x.IndexDocument;
import org.openzal.zal.index.x.IndexSearcher;
import org.openzal.zal.index.x.IndexStore;
import org.openzal.zal.index.x.Indexer;

import javax.annotation.Nonnull;

/* $if ZimbraX == 1 $
import java.lang.reflect.Field;
import com.zimbra.common.service.ServiceException;
import org.openzal.zal.exceptions.ExceptionWrapper;
/* $endif $ */

public class SolrIndexStore
  extends IndexStore
{

  public SolrIndexStore(@Nonnull MailboxIndex mailboxIndex, @Nonnull Object zObject)
  {
    /* $if ZimbraX == 1 $
    super(mailboxIndex, (com.zimbra.cs.index.solr.SolrIndex) zObject);

    /* $else $ */
    super(mailboxIndex, null);

    /* $endif $ */
  }

  @Override
  public IndexDocument createDocument()
  {
    /* $if ZimbraX == 1 $
    {
      return new SolrIndexDocument();
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  @Override
  public Indexer openIndexer()
    throws IOException, ZimbraException
  {
    /* $if ZimbraX == 1 $
    {
      try
      {
        return new SolrIndexer(
          getMailboxIndex().getMailbox().getAccountId(),
          getRequestHelper(),
          toZimbra(com.zimbra.cs.index.solr.SolrIndex.class).openIndexer()
        );
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

  @Override
  public IndexSearcher openSearcher()
    throws IOException, ZimbraException
  {
    /* $if ZimbraX == 1 $
    {
      try
      {
        return new SolrIndexSearcher(
          getMailboxIndex().getMailbox().getAccountId(),
          getRequestHelper(),
          toZimbra(com.zimbra.cs.index.solr.SolrIndex.class).openSearcher()
        );
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

  public SolrRequestHelper getRequestHelper()
  {
    /* $if ZimbraX == 1 $
    {
      try
      {
        Field field = com.zimbra.cs.index.solr.SolrIndex.class.getDeclaredField("solrHelper");
        field.setAccessible(true);

        return new SolrRequestHelper(field.get(toZimbra(com.zimbra.cs.index.solr.SolrIndex.class)));
      }
      catch( Exception e )
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
}
