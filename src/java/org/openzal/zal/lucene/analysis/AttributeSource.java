package org.openzal.zal.lucene.analysis;

import javax.annotation.Nonnull;
import org.openzal.zal.lucene.analysis.tokenattributes.CharTermAttribute;


public class AttributeSource
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final org.apache.lucene.util.AttributeSource mZObject;
  /* $endif $ */

  public AttributeSource(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject = (org.apache.lucene.util.AttributeSource) zObject;
    /* $endif $ */
  }

  public CharTermAttribute addCharTermAttribute()  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return new CharTermAttribute(mZObject.addAttribute(org.apache.lucene.analysis.tokenattributes.CharTermAttribute.class));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString() {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(
    @Nonnull Class<T> target
  )
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
