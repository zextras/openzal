package org.openzal.zal.lucene.document;

import org.jetbrains.annotations.NotNull;

/* $if ZimbraVersion >= 8.5.0 $ */
public class LuceneDocument
{
  private final com.zimbra.cs.index.IndexDocument mZObject;

  public LuceneDocument(@NotNull Object zObject)
  {
    mZObject = (com.zimbra.cs.index.IndexDocument) zObject;
  }

  public LuceneDocument()
  {
    this(new com.zimbra.cs.index.IndexDocument());
  }

  public void add(
    @NotNull String field,
    @NotNull String value,
    @NotNull LuceneField.Store stored,
    @NotNull LuceneField.Index indexed
  )
  {
    add(new LuceneField(field, value, stored, indexed));
  }

  public void add(LuceneField field)
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
    return target.cast(mZObject);
  }
}
/* $else $
public class LuceneDocument
{
  public LuceneDocument(@NotNull Object zObject)
  {
    throw new UnsupportedOperationException();
  }

  public LuceneDocument()
  {
    throw new UnsupportedOperationException();
  }

  public void add(@NotNull String field, @NotNull String value, @NotNull LuceneField.Store stored, @NotNull LuceneField.Index indexed)
  {
    throw new UnsupportedOperationException();
  }

  public void add(LuceneField field)
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