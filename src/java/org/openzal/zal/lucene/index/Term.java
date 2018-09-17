package org.openzal.zal.lucene.index;

import org.jetbrains.annotations.NotNull;

public class Term
{
  private final org.apache.lucene.index.Term mZObject;

  public Term(@NotNull String field, @NotNull String value)
  {
    this(new org.apache.lucene.index.Term(field, value));
  }

  public Term(@NotNull Object zObject)
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

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }
}
