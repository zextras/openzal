package org.openzal.zal.lucene.search;

import java.io.Serializable;

public class BooleanClause
  implements Serializable
{
  // TODO: implemented missing content if necessary

  public static enum Occur
  {
    MUST
      {
        public String toString()
        {
          return "+";
        }
      },
    SHOULD
      {
        public String toString()
        {
          return "";
        }
      },
    MUST_NOT
      {
        public String toString()
        {
          return "-";
        }
      };

    private Occur()
    {
    }
  }
}
