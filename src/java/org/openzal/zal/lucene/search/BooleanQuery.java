package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;
import org.openzal.zal.ZimbraListWrapper;

import java.util.List;

public class BooleanQuery
  extends Query
{
  public BooleanQuery()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    this(new org.apache.lucene.search.BooleanQuery());
    /* $else $
    this((Object) null);
    /* $endif $ */
  }

  public BooleanQuery(@Nonnull
    Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    super((org.apache.lucene.search.BooleanQuery) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }

  public void add(@Nonnull Query query, BooleanClause.Occur occur)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    toZimbra(org.apache.lucene.search.BooleanQuery.class).add(query.toZimbra(org.apache.lucene.search.Query.class), org.apache.lucene.search.BooleanClause.Occur.valueOf(occur.name()));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public int clausesSize()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return toZimbra(org.apache.lucene.search.BooleanQuery.class).clauses().size();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
