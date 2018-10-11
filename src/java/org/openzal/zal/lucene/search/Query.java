package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;

public class Query
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final org.apache.lucene.search.Query mZObject;
  /* $endif $ */

  public Query(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject = (org.apache.lucene.search.Query) zObject;
    /* $endif $ */
  }

  /* $if ZimbraVersion >= 8.5.0 $ */
  protected org.apache.lucene.search.Query getZimbra()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
  /* $endif $ */

  public Query combine(Query... queries)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    org.apache.lucene.search.Query[] zimbraQueries = new org.apache.lucene.search.Query[queries.length];

    for( int i = 0; i < queries.length; i++ )
    {
      zimbraQueries[i] = queries[i].toZimbra(org.apache.lucene.search.Query.class);
    }

    return new Query(getZimbra().combine(zimbraQueries));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
