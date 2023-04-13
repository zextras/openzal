package org.openzal.zal.lucene.analysis;

import javax.annotation.Nonnull;
import org.openzal.zal.lucene.analysis.tokenattributes.CharTermAttribute;


public class AttributeSource
{
  private final org.apache.lucene.util.AttributeSource mZObject;

  public AttributeSource(@Nonnull Object zObject)
  {
    mZObject = (org.apache.lucene.util.AttributeSource) zObject;
  }

  public CharTermAttribute addCharTermAttribute()  {
    return new CharTermAttribute(mZObject.addAttribute(org.apache.lucene.analysis.tokenattributes.CharTermAttribute.class));
  }

  @Override
  public String toString() {
    return mZObject.toString();
  }

  public <T> T toZimbra(
    @Nonnull Class<T> target
  )
  {
    return target.cast(mZObject);
  }
}
