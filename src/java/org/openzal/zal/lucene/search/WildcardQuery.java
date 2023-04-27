package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;
import org.openzal.zal.lucene.index.Term;

public class WildcardQuery
  extends MultiTermQuery
{
  public WildcardQuery(String field, String value)
  {
    this(new Term(field, value));
  }

  public WildcardQuery(Term term)
  {
    this(new org.apache.lucene.search.WildcardQuery(term.toZimbra(org.apache.lucene.index.Term.class)));
  }

  public WildcardQuery(@Nonnull Object zObject)
  {
    super((org.apache.lucene.search.WildcardQuery) zObject);
  }
}
