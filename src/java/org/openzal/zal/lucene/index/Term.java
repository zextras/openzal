package org.openzal.zal.lucene.index;

import org.jetbrains.annotations.NotNull;

public class Term
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final org.apache.lucene.index.Term mZObject;
  /* $endif $ */

  public Term(@NotNull String field, @NotNull String value)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    this(new org.apache.lucene.index.Term(field, value));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public Term(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject = (org.apache.lucene.index.Term) zObject;
    /* $else $
    throw new UnsupportedOperationException();
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

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
