package org.openzal.zal.lucene.analysis.tokenattributes;

import javax.annotation.Nonnull;

public class CharTermAttribute
{
  private final org.apache.lucene.analysis.tokenattributes.CharTermAttribute mZObject;

  public CharTermAttribute(
    @Nonnull
      Object zObject
  )
  {
    mZObject = (org.apache.lucene.analysis.tokenattributes.CharTermAttribute) zObject;
  }

  @Override
  public String toString()
  {
    return mZObject.toString();
  }

  public <T> T toZimbra(
    @Nonnull
      Class<T> target
  )
  {
    return target.cast(mZObject);
  }
}
