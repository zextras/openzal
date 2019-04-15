package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;

public class MultiTermQuery
  extends Query
{
  public MultiTermQuery(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super((org.apache.lucene.search.MultiTermQuery) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }
}
