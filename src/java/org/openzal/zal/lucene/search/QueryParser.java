package org.openzal.zal.lucene.search;

public class QueryParser
{

  public static String escape(String s)
  {
    /* $if ZimbraX == 1 $ */
    throw new UnsupportedOperationException();
    /* $else $
    return org.apache.lucene.queryParser.QueryParser.escape(s);
    /* $endif $ */
  }
}
