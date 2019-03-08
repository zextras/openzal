package org.openzal.zal.lucene.document;

import org.jetbrains.annotations.NotNull;

public class Field
{
  /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
  private final org.apache.lucene.document.Field mZObject;
  /* $endif $ */

  public Field(@NotNull String name, @NotNull String value, @NotNull Store stored, @NotNull Index indexed)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    this(new org.apache.lucene.document.Field(
      name,
      value,
      org.apache.lucene.document.Field.Store.valueOf(stored.name()),
      org.apache.lucene.document.Field.Index.valueOf(indexed.name())
    ));
    /* $endif $ */
  }

  public Field(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mZObject = (org.apache.lucene.document.Field) zObject;
    /* $endif $ */
  }

  public String getName()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return mZObject.name();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public String getValue()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return mZObject.stringValue();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public boolean isStored()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return mZObject.isStored();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public boolean isIndexed()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return mZObject.isIndexed();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return mZObject.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public enum Store
  {
    YES,
    NO
  }

  public enum Index
  {
    NO,
    ANALYZED,
    NOT_ANALYZED,
    ANALYZED_NO_NORMS,
    NOT_ANALYZED_NO_NORMS
  }
}
