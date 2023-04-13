package org.openzal.zal.lucene.analysis;

import javax.annotation.Nonnull;

public class ZimbraAnalyzer extends Analyzer
{
  public ZimbraAnalyzer(@Nonnull Object zObject)
  {
    super((com.zimbra.cs.index.ZimbraAnalyzer) zObject);
  }

  public static Analyzer getAnalyzer(String name)
  {
    return new Analyzer(com.zimbra.cs.index.ZimbraAnalyzer.getAnalyzer(name));
  }

  public static Analyzer getInstance()
  {
    return new Analyzer(com.zimbra.cs.index.ZimbraAnalyzer.getInstance());
  }
}
