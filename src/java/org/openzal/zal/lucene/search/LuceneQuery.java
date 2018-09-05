package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lib.ZalWrapper;

public abstract class LuceneQuery
{
  private final org.apache.lucene.search.Query mZObject;

  public LuceneQuery(Object zObject)
  {
    mZObject = (org.apache.lucene.search.Query) zObject;
  }

  @Override
  public String toString()
  {
    return mZObject.toString();
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }
}
