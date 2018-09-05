package org.openzal.zal.lucene.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.openzal.zal.lucene.index.LuceneTerm;

public class LuceneTermQuery
  extends LuceneQuery
{
  public LuceneTermQuery(LuceneTerm term)
  {
    super(new TermQuery(term.toZimbra(org.apache.lucene.index.Term.class)));
  }
}
