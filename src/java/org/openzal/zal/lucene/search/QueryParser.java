package org.openzal.zal.lucene.search;

public class QueryParser
{

  public static String escape(String s)
  {
    return org.apache.lucene.queryParser.QueryParser.escape(s);
  }
}
