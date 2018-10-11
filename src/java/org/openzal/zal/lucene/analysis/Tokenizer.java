package org.openzal.zal.lucene.analysis;

import org.jetbrains.annotations.NotNull;

public class Tokenizer
  extends TokenStream
{
  public Tokenizer(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super((org.apache.lucene.analysis.TokenStream) zObject);
    /* $else $
    super((Object) null);
    /* $endif $ */
  }
}
