package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;
import org.openzal.zal.lucene.index.Term;

public class TermQuery
  extends Query
{

  public TermQuery(Term term)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super(new org.apache.lucene.search.TermQuery(term.toZimbra(org.apache.lucene.index.Term.class)));
    /* $else $
    this((Object) null);
    /* $endif $ */
  }

  public TermQuery(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super((org.apache.lucene.search.TermQuery) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }
}
