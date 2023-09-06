package org.openzal.zal.lucene.document;

import javax.annotation.Nonnull;

public class DocumentId
{
  private com.zimbra.cs.index.ZimbraIndexDocumentID mDocumentID;

  public DocumentId(@Nonnull Object zObject)
  {
    mDocumentID = (com.zimbra.cs.index.ZimbraIndexDocumentID) zObject;
  }

  @Override
  public String toString()
  {
    return mDocumentID.toString();
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    return target.cast(mDocumentID);
  }
}
