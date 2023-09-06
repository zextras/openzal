package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;
import org.openzal.zal.lucene.index.Term;

public class PrefixQuery
  extends MultiTermQuery
{
  public PrefixQuery(Term term)
  {
    super(new org.apache.lucene.search.PrefixQuery(term.toZimbra(org.apache.lucene.index.Term.class)));
  }

  public PrefixQuery(@Nonnull Object zObject)
  {
    super((org.apache.lucene.search.PrefixQuery) zObject);
  }

  public Term getPrefix()
  {
    return new Term(toZimbra(org.apache.lucene.search.PrefixQuery.class).getPrefix());
  }

  @Override
  public String toString()
  {
    return toZimbra(org.apache.lucene.search.PrefixQuery.class).toString();
  }

  @Override
  public int hashCode()
  {
    return toZimbra(org.apache.lucene.search.PrefixQuery.class).hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    return toZimbra(org.apache.lucene.search.PrefixQuery.class).equals(obj);
  }
}
