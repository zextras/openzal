package org.openzal.zal.lucene.document;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* $if ZimbraVersion >= 8.5.0 $ */
public class Document
{
  private final com.zimbra.cs.index.IndexDocument mZObject;

  public Document(@NotNull Object zObject)
  {
    if( zObject instanceof org.apache.lucene.document.Document )
    {
      zObject = new com.zimbra.cs.index.IndexDocument((org.apache.lucene.document.Document) zObject);
    }
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

  public String get(String name)
  {
    return mZObject.toDocument().get(name);
  }

  public Field getField(String name)
  {
    return new Field(mZObject.toDocument().getField(name));
  }

  public List<Field> getFields(String name)
  {
    List<Field> fieldList = new ArrayList<>();

    for(org.apache.lucene.document.Field field : mZObject.toDocument().getFields(name))
    {
      fieldList.add(new Field(field));
    }

    return fieldList;
  }

  public Set<String> getFieldIds()
  {
    Set<String> fieldIdList = new HashSet<>();

    for(org.apache.lucene.document.Fieldable field : mZObject.toDocument().getFields())
    {
      fieldIdList.add(field.name());
    }

    return fieldIdList;
  }

  @Override
  public String toString()
  {
    return mZObject.toDocument().toString();
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    if( target.equals(org.apache.lucene.document.Document.class) )
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

  public String get(String name)
  {
    throw new UnsupportedOperationException();
  }

  public Field getField(String name)
  {
    throw new UnsupportedOperationException();
  }

  public List<Field> getFields(String name)
  {
    throw new UnsupportedOperationException();
  }

  public Set<String> getFieldIds()
  {
    throw new UnsupportedOperationException();
  }

  public String toString()
  {
    throw new UnsupportedOperationException();
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    throw new UnsupportedOperationException();
  }
}
/* $endif $ */