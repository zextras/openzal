package org.openzal.zal.lucene.analysis;

import javax.annotation.Nonnull;

public class CharFilter
  extends CharStream
{

  public CharFilter(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super((org.apache.lucene.analysis.CharFilter) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }
}
