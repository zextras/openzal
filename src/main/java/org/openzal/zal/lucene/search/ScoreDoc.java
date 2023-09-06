package org.openzal.zal.lucene.search;

import org.openzal.zal.lucene.document.Document;
import org.openzal.zal.lucene.document.DocumentId;

import javax.annotation.Nonnull;

public class ScoreDoc
{
  private Document                           mIndexDocument;
  private com.zimbra.cs.index.ZimbraScoreDoc mScoreDoc;

  @Deprecated
  public ScoreDoc(@Nonnull Object zObject)
  {
    this(null, zObject);
  }

  public ScoreDoc(Document indexDocument, @Nonnull Object zObject)
  {
    mIndexDocument = indexDocument;
    mScoreDoc = (com.zimbra.cs.index.ZimbraScoreDoc) zObject;
  }

  public DocumentId getDocumentId()
  {
    return new DocumentId(mScoreDoc.getDocumentID());
  }

  public float getScore()
  {
    return mScoreDoc.getScore();
  }

  public Document getDocument()
  {
    return mIndexDocument;
  }

  @Override
  public String toString()
  {
    return mScoreDoc.toString();
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    return target.cast(mScoreDoc);
  }
}