package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;

public class MultiTermQuery
  extends Query
{
  public MultiTermQuery(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super((org.apache.lucene.search.MultiTermQuery) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }
}
