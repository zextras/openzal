package org.openzal.zal.lucene.document;

import org.jetbrains.annotations.NotNull;

/* $if ZimbraVersion >= 8.5.0 $ */
public class Document
{
  private final com.zimbra.cs.index.IndexDocument mZObject;

  public Document(@NotNull Object zObject)
  {
    mZObject = (com.zimbra.cs.index.IndexDocument) zObject;
  }

  public Document()
  {
    this(new com.zimbra.cs.index.IndexDocument());
  }

  public void add(
    @NotNull String field,
    @NotNull String value,
    @NotNull Field.Store stored,
    @NotNull Field.Index indexed
  )
  {
    add(new Field(field, value, stored, indexed));
  }

  public void add(Field field)
  {
    mZObject.toDocument().add(field.toZimbra(org.apache.lucene.document.Field.class));
  }

  public void remove(String field)
  {
    mZObject.toDocument().removeField(field);
  }

  public void removeAll(String field)
  {
    mZObject.toDocument().removeFields(field);
  }

  @Override
  public String toString()
  {
    return mZObject.toDocument().toString();
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    if(target.equals(org.apache.lucene.document.Document.class))
    {
      return target.cast(mZObject.toDocument());
    }

    return target.cast(mZObject);
  }
}
/* $else $
public class Document
{
  public Document(@NotNull Object zObject)
  {
    throw new UnsupportedOperationException();
  }

  public Document()
  {
    throw new UnsupportedOperationException();
  }

  public void add(@NotNull String field, @NotNull String value, @NotNull Field.Store stored, @NotNull Field.Index indexed)
  {
    throw new UnsupportedOperationException();
  }

  public void add(Field field)
  {
    throw new UnsupportedOperationException();
  }

  public void remove(String field)
  {
    throw new UnsupportedOperationException();
  }

  public void removeAll(String field)
  {
    throw new UnsupportedOperationException();
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    throw new UnsupportedOperationException();
  }
}
/* $endif $ */