package org.openzal.zal.lucene.index;

import javax.annotation.Nonnull;

public class Term
{
  private final org.apache.lucene.index.Term mZObject;

  public Term(@Nonnull String field, @Nonnull String value)
  {
    this(new org.apache.lucene.index.Term(field, value));
  }

  public Term(@Nonnull Object zObject)
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

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    return target.cast(mZObject);
  }
}
