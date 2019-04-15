package org.openzal.zal.lucene.analysis.tokenattributes;

import javax.annotation.Nonnull;

public class CharTermAttribute
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final org.apache.lucene.analysis.tokenattributes.CharTermAttribute mZObject;
  /* $endif $ */

  public CharTermAttribute(
    @Nonnull
      Object zObject
  )
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject = (org.apache.lucene.analysis.tokenattributes.CharTermAttribute) zObject;
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(
    @Nonnull
      Class<T> target
  )
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
