package org.openzal.zal.lucene.search;

import org.openzal.zal.lucene.document.Document;
import org.openzal.zal.lucene.document.DocumentId;

import javax.annotation.Nonnull;

public class ScoreDoc
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private Document                           mIndexDocument;
  private com.zimbra.cs.index.ZimbraScoreDoc mScoreDoc;
  /* $endif $ */

  @Deprecated
  public ScoreDoc(@Nonnull Object zObject)
  {
    this(null, zObject);
  }

  public ScoreDoc(Document indexDocument, @Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mIndexDocument = indexDocument;
    mScoreDoc = (com.zimbra.cs.index.ZimbraScoreDoc) zObject;
    /* $endif $ */
  }

  public DocumentId getDocumentId()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return new DocumentId(mScoreDoc.getDocumentID());
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public float getScore()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mScoreDoc.getScore();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public Document getDocument()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mIndexDocument;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mScoreDoc.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mScoreDoc);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}