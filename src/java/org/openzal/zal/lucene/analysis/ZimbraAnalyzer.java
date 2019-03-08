package org.openzal.zal.lucene.analysis;

import org.jetbrains.annotations.NotNull;

public class ZimbraAnalyzer extends Analyzer
{
  public ZimbraAnalyzer(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    super((com.zimbra.cs.index.ZimbraAnalyzer) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }

  public static Analyzer getAnalyzer(String name)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return new Analyzer(com.zimbra.cs.index.ZimbraAnalyzer.getAnalyzer(name));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public static Analyzer getInstance()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return new Analyzer(com.zimbra.cs.index.ZimbraAnalyzer.getInstance());
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
