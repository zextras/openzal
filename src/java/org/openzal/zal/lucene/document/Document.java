package org.openzal.zal.lucene.document;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Document
{
  /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
  private final com.zimbra.cs.index.IndexDocument mZObject;
  /* $endif $ */

  public Document(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    if( zObject instanceof org.apache.lucene.document.Document )
    {
      zObject = new com.zimbra.cs.index.IndexDocument((org.apache.lucene.document.Document) zObject);
    }
    mZObject = (com.zimbra.cs.index.IndexDocument) zObject;
    /* $endif $ */
  }

  public Document()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    this(new com.zimbra.cs.index.IndexDocument());
    /* $endif $ */
  }

  public void add(
    @Nonnull String field,
    @Nonnull String value,
    @Nonnull Field.Store stored,
    @Nonnull Field.Index indexed
  )
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    add(new Field(field, value, stored, indexed));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void add(Field field)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mZObject.toDocument().add(field.toZimbra(org.apache.lucene.document.Field.class));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void remove(String field)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mZObject.toDocument().removeField(field);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void removeAll(String field)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mZObject.toDocument().removeFields(field);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public String get(String name)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return mZObject.toDocument().get(name);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public Field getField(String name)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return new Field(mZObject.toDocument().getField(name));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public List<Field> getFields(String name)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    List<Field> fieldList = new ArrayList<>();

    for( org.apache.lucene.document.Field field : mZObject.toDocument().getFields(name) )
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

    for( org.apache.lucene.document.Fieldable field : mZObject.toDocument().getFields() )
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
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return mZObject.toDocument().toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    if( target.equals(org.apache.lucene.document.Document.class) )
    {
      return target.cast(mZObject.toDocument());
    }

    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}