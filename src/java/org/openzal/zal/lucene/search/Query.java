package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;

public class Query
{
  private final org.apache.lucene.search.Query mZObject;

  public Query(@NotNull Object zObject)
  {
    mZObject = (org.apache.lucene.search.Query) zObject;
  }

  protected org.apache.lucene.search.Query getZimbra()
  {
    return mZObject;
  }

  public Query combine(Query...queries)
  {
    org.apache.lucene.search.Query[] zimbraQueries = new org.apache.lucene.search.Query[queries.length];

    for(int i = 0; i < queries.length; i++)
    {
      zimbraQueries[i] = queries[i].toZimbra(org.apache.lucene.search.Query.class);
    }

    return new Query(getZimbra().combine(zimbraQueries));
  }

  @Override
  public String toString()
  {
    return mZObject.toString();
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }
}
