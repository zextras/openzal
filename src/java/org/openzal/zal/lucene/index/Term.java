package org.openzal.zal.lucene.index;

import javax.annotation.Nonnull;

public class Term
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final org.apache.lucene.index.Term mZObject;
  /* $endif $ */

  public Term(@Nonnull String field, @Nonnull String value)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    this(new org.apache.lucene.index.Term(field, value));
    /* $endif $ */
  }

  public Term(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject = (org.apache.lucene.index.Term) zObject;
    /* $endif $ */
  }

  public String getField()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.field();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public String getValue()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.text();
    /* $else $
    throw new UnsupportedOperationException();
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

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
