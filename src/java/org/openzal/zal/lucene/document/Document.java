package org.openzal.zal.lucene.document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

public class Document
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private com.zimbra.cs.index.IndexDocument mIndexDocument;
  /* $endif $ */

  public Document()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    this(new com.zimbra.cs.index.IndexDocument());
    /* $endif $ */
  }

  public Document(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    if(zObject instanceof org.apache.lucene.document.Document)
    {
      zObject = new com.zimbra.cs.index.IndexDocument((org.apache.lucene.document.Document) zObject);
    }
    /* $endif $ */

    /* $if ZimbraVersion >= 8.5.0 $ */
    mIndexDocument = (com.zimbra.cs.index.IndexDocument) zObject;
    /* $endif $ */
  }

  public void add(@Nonnull String field, @Nonnull String value)
  {
    /* $if ZimbraX == 1 $
    mIndexDocument.toInputDocument().addField(field, value);
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    add(field, value, Field.Store.YES);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void add(@Nonnull String field, @Nonnull String value, @Nonnull Field.Store stored)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    add(field, value, stored, Field.Index.ANALYZED);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void add(@Nonnull String field, @Nonnull String value, @Nonnull Field.Store stored, @Nonnull Field.Index indexed)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    add(new Field(field, value, stored, indexed));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void add(Field field)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mIndexDocument.toDocument().add(field.toZimbra(org.apache.lucene.document.Field.class));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public boolean has(String field)
  {
    /* $if ZimbraX == 1 $
    return mIndexDocument.toInputDocument().containsKey(field);
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    return mIndexDocument.toDocument().get(field) != null;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public String get(String field)
  {
    /* $if ZimbraX == 1 $
    return (String) mIndexDocument.toInputDocument().getFieldValue(field);
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    return mIndexDocument.toDocument().get(field);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void remove(String field)
  {
    /* $if ZimbraX == 1 $
    mIndexDocument.toInputDocument().removeField(field);
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    mIndexDocument.toDocument().removeField(field);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void removeAll(String field)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mIndexDocument.toDocument().removeFields(field);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public Field getField(String name)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return new Field(mIndexDocument.toDocument().getField(name));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public List<Field> getFields(String name)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    List<Field> fieldList = new ArrayList<>();

    for( org.apache.lucene.document.Field field : mIndexDocument.toDocument().getFields(name) )
    {
      fieldList.add(new Field(field));
    }

    return fieldList;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public Set<String> getFieldIds()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    Set<String> fieldIdList = new HashSet<>();

    for( org.apache.lucene.document.Fieldable field : mIndexDocument.toDocument().getFields() )
    {
      fieldIdList.add(field.name());
    }

    return fieldIdList;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraX == 1 $
    return mIndexDocument.toInputDocument().toString();
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    return mIndexDocument.toDocument().toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target) // TODO: finish to fix Analyzer
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mIndexDocument);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}