package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;

public class BooleanQuery
  extends Query
{
  @Deprecated
  public BooleanQuery()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    this(new org.apache.lucene.search.BooleanQuery());

    /* $else $
    this((Object) null);

    /* $endif $ */
  }

  @Deprecated
  public BooleanQuery(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ && ZimbraX == 0 */
    super(zObject);

    /* $else $
    super(null);

    /* $endif $ */
  }

  public void add(@Nonnull Query query, BooleanClause.Occur occur)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    {
      toZimbra(org.apache.lucene.search.BooleanQuery.class).add(query.toZimbra(org.apache.lucene.search.Query.class), org.apache.lucene.search.BooleanClause.Occur.valueOf(occur.name()));
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public int clausesSize()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    {
      return toZimbra(org.apache.lucene.search.BooleanQuery.class).clauses().size();
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public static class Builder
  {
    /* $if ZimbraX == 1 $ */
    private final org.apache.lucene.search.BooleanQuery.Builder mZObject;
    /* $endif $ */

    public Builder()
    {
      /* $if ZimbraX == 1 $ */
      this(new org.apache.lucene.search.BooleanQuery.Builder());

      /* $endif $ */
    }

    public Builder(@Nonnull Object zObject)
    {
      /* $if ZimbraX == 1 $ */
      {
        mZObject = (org.apache.lucene.search.BooleanQuery.Builder) zObject;
      }
      /* $endif $ */
    }

    public void add(@Nonnull Query query, BooleanClause.Occur occur)
    {
      /* $if ZimbraX == 1 $ */
      {
        mZObject.add(query.toZimbra(org.apache.lucene.search.Query.class), org.apache.lucene.search.BooleanClause.Occur.valueOf(occur.name()));
      }
      /* $else $
      {
      throw new UnsupportedOperationException();
      }
      /* $endif $ */
    }

    public BooleanQuery build()
    {
      /* $if ZimbraX == 1 $ */
      {
        return new BooleanQuery(mZObject.build());
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
      /* $if ZimbraX == 1 $ */
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
      /* $if ZimbraX == 1 $ */
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
}
