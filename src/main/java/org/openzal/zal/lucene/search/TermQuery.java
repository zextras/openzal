package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;
import org.openzal.zal.lucene.index.Term;

public class TermQuery
  extends Query
{

  public TermQuery(Term term)
  {
    super(new org.apache.lucene.search.TermQuery(term.toZimbra(org.apache.lucene.index.Term.class)));
  }

  public TermQuery(@Nonnull Object zObject)
  {
    super((org.apache.lucene.search.TermQuery) zObject);
  }
}
