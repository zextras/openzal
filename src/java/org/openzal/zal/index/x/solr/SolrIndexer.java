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

import com.zimbra.common.service.ServiceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.openzal.zal.Item;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.index.x.IndexDocument;
import org.openzal.zal.index.x.Indexer;

import javax.annotation.Nonnull;

public class SolrIndexer
  extends Indexer
{
  /* $if ZimbraX == 1 $
  private SolrRequestHelper mRequestHelper;
  /* $endif $ */

  public SolrIndexer(@Nonnull String accountId, @Nonnull SolrRequestHelper requestHelper, @Nonnull Object zObject)
  {
    /* $if ZimbraX == 1 $
    super(accountId, (com.zimbra.cs.index.Indexer) zObject);

    mRequestHelper = requestHelper;

    /* $else $ */
    super(accountId, null);

    /* $endif $ */
  }

  @Override
  public void addDocument(IndexDocument document, Object... idParts)
    throws IOException, ZimbraException
  {
    /* $if ZimbraX == 1 $
    {
      SolrUpdateRequest request = new SolrUpdateRequest();

      document.add("id", mRequestHelper.getId(getAccountId(), idParts));

      request.add(document);
      mRequestHelper.executeUpdate(getAccountId(), request);
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  @Override
  public void addDocument(Iterable<IndexDocument> documents, Item item)
    throws IOException, ZimbraException
  {
    /* $if ZimbraX == 1 $
    {
      ArrayList<com.zimbra.cs.index.IndexDocument> zimbraDocuments = new ArrayList<>();

      com.zimbra.cs.index.IndexDocument zimbraDocument;
      for( Iterator<IndexDocument> iterator = documents.iterator(); iterator.hasNext(); zimbraDocuments.add(zimbraDocument) )
      {
        zimbraDocument = iterator.next().toZimbra(com.zimbra.cs.index.IndexDocument.class);
      }

      try
      {
        toZimbra(com.zimbra.cs.index.Indexer.class).addDocument(item.toZimbra(com.zimbra.cs.mailbox.MailItem.class), zimbraDocuments);
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
  public void addDocuments(Iterable<IndexDocument> documents, Object... idParts)
    throws IOException, ZimbraException
  {
    /* $if ZimbraX == 1 $
    {
      List<Object> idPartList = Arrays.asList(idParts);
      int partNum = 1;

      Iterator<IndexDocument> iterator = documents.iterator();
      while( iterator.hasNext() )
      {
        idPartList.set(idParts.length, partNum++);

        addDocument(iterator.next(), idPartList.toArray());
      }
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  @Override
  public void deleteDocument(@Nonnull Object... idParts)
    throws ZimbraException
  {
    /* $if ZimbraX == 1 $
    {
      SolrUpdateRequest request = new SolrUpdateRequest();

      String id = mRequestHelper.getId(getAccountId(), idParts);

      BooleanQuery.Builder disjunctionBuilder = new BooleanQuery.Builder();
      disjunctionBuilder.add(new TermQuery(new Term("id", id)), BooleanClause.Occur.SHOULD);

      BooleanQuery.Builder queryBuilder;
      if( mRequestHelper.needsAccountFilter() )
      {
        queryBuilder = new BooleanQuery.Builder();
        queryBuilder.add(new TermQuery(new Term("acct_id", getAccountId())), BooleanClause.Occur.MUST);
        queryBuilder.add(disjunctionBuilder.build(), BooleanClause.Occur.MUST);
      }
      else
      {
        queryBuilder = disjunctionBuilder;
      }

      request.deleteByQuery(queryBuilder.build().toString());

      mRequestHelper.executeUpdate(getAccountId(), request);
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }
}
