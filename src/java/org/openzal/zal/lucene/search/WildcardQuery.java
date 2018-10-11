package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lucene.index.Term;

public class WildcardQuery
  extends MultiTermQuery
{
  public WildcardQuery(Term term)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    this(new org.apache.lucene.search.WildcardQuery(term.toZimbra(org.apache.lucene.index.Term.class)));
    /* $else $
    this((Object) null);
    /* $endif $ */
  }

  public WildcardQuery(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super((org.apache.lucene.search.WildcardQuery) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }
}
