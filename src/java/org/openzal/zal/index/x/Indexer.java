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

package org.openzal.zal.index.x;

import java.io.Closeable;
import java.io.IOException;
import org.openzal.zal.Item;
import org.openzal.zal.exceptions.ZimbraException;

import javax.annotation.Nonnull;

public abstract class Indexer
  extends org.openzal.zal.lucene.index.Indexer
  implements Closeable
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final String                      mAccountId;
  private final com.zimbra.cs.index.Indexer mZObject;
  /* $endif $ */

  public Indexer(@Nonnull String accountId, @Nonnull Object zObject)
  {
    super(null);

    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      mAccountId = accountId;
      mZObject = (com.zimbra.cs.index.Indexer) zObject;
    }
    /* $endif $ */
  }

  public abstract void addDocument(IndexDocument document, Object... idParts)
    throws IOException, ZimbraException;

  public abstract void addDocuments(Iterable<IndexDocument> documents, Object... idParts)
    throws IOException, ZimbraException;

  public abstract void addDocument(Iterable<IndexDocument> documents, Item item)
    throws IOException, ZimbraException;

  public abstract void deleteDocument(@Nonnull Object... idParts)
    throws IOException, ZimbraException;

  @Override
  public void close()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      mZObject.close();
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public String getAccountId()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      return mAccountId;
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      return mZObject.toString();
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      return target.cast(mZObject);
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }
}
