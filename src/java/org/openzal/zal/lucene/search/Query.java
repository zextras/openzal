package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;

public class Query
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final Object mQuery;
  /* $endif $ */

  public Query(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mQuery = zObject;
    /* $endif $ */
  }

  @Deprecated
  public Query combine(Query... queries)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    org.apache.lucene.search.Query[] zimbraQueries = new org.apache.lucene.search.Query[queries.length];

    for( int i = 0; i < queries.length; i++ )
    {
      zimbraQueries[i] = queries[i].toZimbra(org.apache.lucene.search.Query.class);
    }

    return new Query(toZimbra(org.apache.lucene.search.Query.class).combine(zimbraQueries));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mQuery.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mQuery);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
