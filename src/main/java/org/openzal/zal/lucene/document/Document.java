package org.openzal.zal.lucene.document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

public class Document
{
  private com.zimbra.cs.index.IndexDocument mIndexDocument;

  public Document()
  {
    this(new com.zimbra.cs.index.IndexDocument());
  }

  public Document(@Nonnull Object zObject)
  {
    if(zObject instanceof org.apache.lucene.document.Document)
    {
      zObject = new com.zimbra.cs.index.IndexDocument((org.apache.lucene.document.Document) zObject);
    }

    mIndexDocument = (com.zimbra.cs.index.IndexDocument) zObject;
  }

  public void add(@Nonnull String field, @Nonnull String value)
  {
    add(field, value, Field.Store.YES);
  }

  public void add(@Nonnull String field, @Nonnull String value, @Nonnull Field.Store stored)
  {
    add(field, value, stored, Field.Index.ANALYZED);
  }

  public void add(@Nonnull String field, @Nonnull String value, @Nonnull Field.Store stored, @Nonnull Field.Index indexed)
  {
    add(new Field(field, value, stored, indexed));
  }

  public void add(Field field)
  {
    mIndexDocument.toDocument().add(field.toZimbra(org.apache.lucene.document.Field.class));
  }

  public boolean has(String field)
  {
    return mIndexDocument.toDocument().get(field) != null;
  }

  public String get(String field)
  {
    return mIndexDocument.toDocument().get(field);
  }

  public void remove(String field)
  {
    mIndexDocument.toDocument().removeField(field);
  }

  public void removeAll(String field)
  {
    mIndexDocument.toDocument().removeFields(field);
  }

  public Field getField(String name)
  {
    return new Field(mIndexDocument.toDocument().getField(name));
  }

  public List<Field> getFields(String name)
  {
    List<Field> fieldList = new ArrayList<>();

    for( org.apache.lucene.document.Field field : mIndexDocument.toDocument().getFields(name) )
    {
      fieldList.add(new Field(field));
    }

    return fieldList;
  }

  public Set<String> getFieldIds()
  {
    Set<String> fieldIdList = new HashSet<>();

    for( org.apache.lucene.document.Fieldable field : mIndexDocument.toDocument().getFields() )
    {
      fieldIdList.add(field.name());
    }

    return fieldIdList;
  }

  @Override
  public String toString()
  {
    return mIndexDocument.toDocument().toString();
  }

  public <T> T toZimbra(@Nonnull Class<T> target) // TODO: finish to fix Analyzer
  {
    return target.cast(mIndexDocument);
  }
}