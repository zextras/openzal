package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lib.ZalWrapper;

public abstract class LuceneQuery
  implements ZalWrapper<org.apache.lucene.search.Query>
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

  @Override
  public org.apache.lucene.search.Query toZimbra()
  {
    return mZObject;
  }

  @Override
  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }
}
