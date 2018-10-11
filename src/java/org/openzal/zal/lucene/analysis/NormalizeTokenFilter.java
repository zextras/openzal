package org.openzal.zal.lucene.analysis;

import org.jetbrains.annotations.NotNull;

public class NormalizeTokenFilter
  extends CharFilter
{
  public NormalizeTokenFilter(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super((com.zimbra.cs.index.analysis.NormalizeTokenFilter) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }

  public static int normalize(int c)
  {
    return com.zimbra.cs.index.analysis.NormalizeTokenFilter.normalize(c);
  }

  public static int normalize(int c, int p)
  {
    return com.zimbra.cs.index.analysis.NormalizeTokenFilter.normalize(c, p);
  }

  public static String normalize(String value)
  {
    return com.zimbra.cs.index.analysis.NormalizeTokenFilter.normalize(value);
  }
}
