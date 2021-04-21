package org.openzal.zal.lucene.document;

import javax.annotation.Nonnull;

public class DocumentId
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private com.zimbra.cs.index.ZimbraIndexDocumentID mDocumentID;
  /* $endif $ */

  public DocumentId(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mDocumentID = (com.zimbra.cs.index.ZimbraIndexDocumentID) zObject;
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mDocumentID.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mDocumentID);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
