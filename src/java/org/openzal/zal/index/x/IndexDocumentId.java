package org.openzal.zal.index.x;

import org.openzal.zal.lucene.document.DocumentId;

import javax.annotation.Nonnull;

public class IndexDocumentId
  extends DocumentId
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private com.zimbra.cs.index.ZimbraIndexDocumentID mZObject;
  /* $endif $ */

  public IndexDocumentId(@Nonnull Object zObject)
  {
    super(null);

    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      mZObject = (com.zimbra.cs.index.ZimbraIndexDocumentID) zObject;
    }
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      return mZObject.toString();
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      return target.cast(mZObject);
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }
}
