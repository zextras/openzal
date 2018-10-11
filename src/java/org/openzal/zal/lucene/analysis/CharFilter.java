package org.openzal.zal.lucene.analysis;

import org.jetbrains.annotations.NotNull;

public class CharFilter
  extends CharStream
{

  public CharFilter(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super((org.apache.lucene.analysis.CharFilter) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }

  /* $if ZimbraVersion >= 8.5.0 $ */
  @Override
  protected org.apache.lucene.analysis.CharFilter getZimbra()
  {
    return (org.apache.lucene.analysis.CharFilter) super.getZimbra();
  }
  /* $endif $ */
}
