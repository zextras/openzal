package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;

public class Query
{
  private final Object mQuery;

  public Query(@Nonnull Object zObject)
  {
    mQuery = zObject;
  }

  @Deprecated
  public Query combine(Query... queries)
  {
    org.apache.lucene.search.Query[] zimbraQueries = new org.apache.lucene.search.Query[queries.length];

    for( int i = 0; i < queries.length; i++ )
    {
      zimbraQueries[i] = queries[i].toZimbra(org.apache.lucene.search.Query.class);
    }

    return new Query(toZimbra(org.apache.lucene.search.Query.class).combine(zimbraQueries));
  }

  @Override
  public String toString()
  {
    return mQuery.toString();
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    return target.cast(mQuery);
  }
}
