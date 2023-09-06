package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;

public class MultiTermQuery
  extends Query
{
  public MultiTermQuery(@Nonnull Object zObject)
  {
    super((org.apache.lucene.search.MultiTermQuery) zObject);
  }
}
