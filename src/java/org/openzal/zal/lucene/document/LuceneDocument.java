package org.openzal.zal.lucene.document;

import org.apache.lucene.document.Field;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lib.ZalWrapper;

public class LuceneDocument
  implements ZalWrapper<com.zimbra.cs.index.IndexDocument>
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

  public void add(@NotNull String field, @NotNull String value, @NotNull LuceneField.Store stored, @NotNull LuceneField.Index indexed)
  {
    add(new LuceneField(field, value, stored, indexed));
  }

  public void add(LuceneField field)
  {
    mZObject.toDocument().add(field.toZimbra());
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

  @Override
  public com.zimbra.cs.index.IndexDocument toZimbra()
  {
    return mZObject;
  }

  @Override
  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }
}
