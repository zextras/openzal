package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;
import org.openzal.zal.lucene.index.Term;

public class WildcardQuery
  extends MultiTermQuery
{
  public WildcardQuery(String field, String value)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    this(new Term(field, value));
    /* $else $
    this((Object) null);
    /* $endif $ */
  }

  public WildcardQuery(Term term)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    this(new org.apache.lucene.search.WildcardQuery(term.toZimbra(org.apache.lucene.index.Term.class)));
    /* $else $
    this((Object) null);
    /* $endif $ */
  }

  public WildcardQuery(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super((org.apache.lucene.search.WildcardQuery) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }
}
