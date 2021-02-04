package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;
import org.openzal.zal.lucene.index.Term;

public class PrefixQuery
  extends MultiTermQuery
{
  public PrefixQuery(Term term)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super(new org.apache.lucene.search.PrefixQuery(term.toZimbra(org.apache.lucene.index.Term.class)));
    /* $else $
    this((Object) null);
    /* $endif $ */
  }

  public PrefixQuery(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super((org.apache.lucene.search.PrefixQuery) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }

  public Term getPrefix()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return new Term(toZimbra(org.apache.lucene.search.PrefixQuery.class).getPrefix());
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return toZimbra(org.apache.lucene.search.PrefixQuery.class).toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public int hashCode()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return toZimbra(org.apache.lucene.search.PrefixQuery.class).hashCode();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public boolean equals(Object obj)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return toZimbra(org.apache.lucene.search.PrefixQuery.class).equals(obj);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
