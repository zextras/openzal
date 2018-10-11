package org.openzal.zal.lucene.analysis;

public class UniversalAnalyzer
  extends Analyzer
{
  public UniversalAnalyzer()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    this(new com.zimbra.cs.index.analysis.UniversalAnalyzer());
    /* $else $
    this(null);
    /* $endif $ */
  }

  public UniversalAnalyzer(Object object)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super((com.zimbra.cs.index.analysis.UniversalAnalyzer) object);
    /* $else $
    super(null);
    /* $endif $ */
  }
}
