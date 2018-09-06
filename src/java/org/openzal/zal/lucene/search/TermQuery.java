package org.openzal.zal.lucene.search;

import org.openzal.zal.lucene.index.Term;

public class TermQuery
  extends Query
{
  public TermQuery(Term term)
  {
    super(new org.apache.lucene.search.TermQuery(term.toZimbra(org.apache.lucene.index.Term.class)));
  }
}
