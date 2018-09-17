package org.openzal.zal.lucene.search;

import com.sun.istack.NotNull;
import org.openzal.zal.lucene.index.Term;

public class WildcardQuery
  extends MultiTermQuery
{
  public WildcardQuery(Term term)
  {
    super(new org.apache.lucene.search.WildcardQuery(term.toZimbra(org.apache.lucene.index.Term.class)));
  }
}
