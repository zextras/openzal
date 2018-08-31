package org.openzal.zal.lucene.index;

import org.apache.lucene.index.Term;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lib.ZalWrapper;

public class LuceneTerm
  implements ZalWrapper<org.apache.lucene.index.Term>
{
  private final org.apache.lucene.index.Term mZObject;

  public LuceneTerm(@NotNull String field, @NotNull String value)
  {
    this(new Term(field, value));
  }

  public LuceneTerm(@NotNull Object zObject)
  {
    mZObject = (org.apache.lucene.index.Term) zObject;
  }

  public String getField()
  {
    return mZObject.field();
  }

  public String getValue()
  {
    return mZObject.text();
  }

  @Override
  public String toString()
  {
    return mZObject.toString();
  }

  @Override
  public org.apache.lucene.index.Term toZimbra()
  {
    return mZObject;
  }

  @Override
  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }
}
