package org.openzal.zal.lucene.document;

import org.apache.lucene.document.Field;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lib.ZalWrapper;

public class LuceneField
  implements ZalWrapper<org.apache.lucene.document.Field>
{
  private final org.apache.lucene.document.Field mZObject;

  public LuceneField(@NotNull String name, @NotNull String value, @NotNull Store stored, @NotNull Index indexed)
  {
    this(new Field(name, value, stored.toZimbra(), indexed.toZimbra()));
  }

  public LuceneField(@NotNull Object zObject)
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

  @Override
  public org.apache.lucene.document.Field toZimbra()
  {
    return mZObject;
  }

  @Override
  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }

  public enum Store
    implements ZalWrapper<org.apache.lucene.document.Field.Store>
  {
    YES,
    NO;

    @Override
    public org.apache.lucene.document.Field.Store toZimbra()
    {
      return org.apache.lucene.document.Field.Store.valueOf(name());
    }

    @Override
    public <T> T toZimbra(@NotNull Class<T> target)
    {
      return target.cast(this);
    }
  }

  public enum Index
    implements ZalWrapper<org.apache.lucene.document.Field.Index>
  {
    NO,
    ANALYZED,
    NOT_ANALYZED,
    ANALYZED_NO_NORMS,
    NOT_ANALYZED_NO_NORMS;

    @Override
    public org.apache.lucene.document.Field.Index toZimbra()
    {
      return org.apache.lucene.document.Field.Index.valueOf(name());
    }

    @Override
    public <T> T toZimbra(@NotNull Class<T> target)
    {
      return target.cast(this);
    }
  }
}
