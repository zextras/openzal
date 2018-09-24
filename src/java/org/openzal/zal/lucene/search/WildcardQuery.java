package org.openzal.zal.lucene.search;

import org.openzal.zal.lucene.index.Term;

public class WildcardQuery
  extends MultiTermQuery
{
  public WildcardQuery(Term term)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super(new org.apache.lucene.search.WildcardQuery(term.toZimbra(org.apache.lucene.index.Term.class)));
    /* $else $
    super(null);
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
