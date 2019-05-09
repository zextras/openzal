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

public class SolrIndexDocument
  extends IndexDocument
{
  public SolrIndexDocument()
  {
    /* $if ZimbraX == 1 $
    this(new com.zimbra.cs.index.IndexDocument());

    /* $else $ */
    super(null);

    /* $endif $ */
  }

  public SolrIndexDocument(@Nonnull Object zObject)
  {
    /* $if ZimbraX == 1 $
    super((com.zimbra.cs.index.IndexDocument) zObject);

    /* $else $ */
    super(null);

    /* $endif $ */
  }

  @Override
  public boolean has(String name)
  {
    /* $if ZimbraX == 1 $
    {
      return toZimbra(com.zimbra.cs.index.IndexDocument.class).toInputDocument().containsKey(name);
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  @Override
  public String get(String name)
  {
    /* $if ZimbraX == 1 $
    {
      return (String) toZimbra(com.zimbra.cs.index.IndexDocument.class).toInputDocument().getFieldValue(name);
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  @Override
  public void add(String name, String value)
  {
    /* $if ZimbraX == 1 $
    {
      toZimbra(com.zimbra.cs.index.IndexDocument.class).toInputDocument().addField(name, value);
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  @Override
  public void set(String name, String value)
  {
    /* $if ZimbraX == 1 $
    {
      toZimbra(com.zimbra.cs.index.IndexDocument.class).toInputDocument().setField(name, value);
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  @Override
  public void remove(String name)
  {
    /* $if ZimbraX == 1 $
    {
      toZimbra(com.zimbra.cs.index.IndexDocument.class).toInputDocument().removeField(name);
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }
}
