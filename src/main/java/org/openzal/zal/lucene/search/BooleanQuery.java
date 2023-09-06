package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;

public class BooleanQuery
  extends Query
{
  @Deprecated
  public BooleanQuery()
  {
    this(new org.apache.lucene.search.BooleanQuery());
  }

  public BooleanQuery(Object zObject)
  {
    super(zObject);
  }

  public void add(@Nonnull Query query, BooleanClause.Occur occur)
  {
    toZimbra(org.apache.lucene.search.BooleanQuery.class).add(query.toZimbra(org.apache.lucene.search.Query.class), org.apache.lucene.search.BooleanClause.Occur.valueOf(occur.name()));
  }

  public int clausesSize()
  {
    return toZimbra(org.apache.lucene.search.BooleanQuery.class).clauses().size();
  }

  public static class Builder
  {
    private final Object mZObject;

    public Builder()
    {
      this(new org.apache.lucene.search.BooleanQuery());
    }

    public Builder(@Nonnull Object zObject)
    {
      mZObject = (org.apache.lucene.search.BooleanQuery) zObject;
    }

    public void add(@Nonnull Query query, BooleanClause.Occur occur)
    {
      toZimbra(org.apache.lucene.search.BooleanQuery.class).add(query.toZimbra(org.apache.lucene.search.Query.class), org.apache.lucene.search.BooleanClause.Occur.valueOf(occur.name()));
    }

    public BooleanQuery build()
    {
      return new BooleanQuery(toZimbra(org.apache.lucene.search.BooleanQuery.class));
    }

    @Override
    public String toString()
    {
      return mZObject.toString();
    }

    public <T> T toZimbra(@Nonnull Class<T> target)
    {
      return target.cast(mZObject);
    }
  }
}
