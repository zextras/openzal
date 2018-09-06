package org.openzal.zal.lucene.document;

import org.jetbrains.annotations.NotNull;

/* $if ZimbraVersion >= 8.5.0 $ */
public class DocumentId
{
  private com.zimbra.cs.index.ZimbraIndexDocumentID mZObject;

  public DocumentId(@NotNull Object zObject)
  {
    mZObject = (com.zimbra.cs.index.ZimbraIndexDocumentID) zObject;
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
}
/* $else $
public class DocumentId
{

  public DocumentId(@NotNull Object zObject)
  {
    throw new UnsupportedOperationException();
  }

  @Override
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