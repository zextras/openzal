package org.openzal.zal.lucene.document;

import org.jetbrains.annotations.NotNull;

public class Field
{
  private final org.apache.lucene.document.Field mZObject;

  public Field(@NotNull String name, @NotNull String value, @NotNull Store stored, @NotNull Index indexed)
  {
    this(new org.apache.lucene.document.Field(
      name,
      value,
      org.apache.lucene.document.Field.Store.valueOf(stored.name()),
      org.apache.lucene.document.Field.Index.valueOf(indexed.name())
    ));
  }

  public Field(@NotNull Object zObject)
  {
    mZObject = (org.apache.lucene.document.Field) zObject;
  }

  public String getName()
  {
    return mZObject.name();
  }

  public String getValue()
  {
    return mZObject.stringValue();
  }

  public boolean isStored()
  {
    return mZObject.isStored();
  }

  public boolean isIndexed()
  {
    return mZObject.isIndexed();
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
