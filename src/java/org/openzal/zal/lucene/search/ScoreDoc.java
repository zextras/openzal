package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lucene.document.DocumentId;

/* $if ZimbraVersion >= 8.5.0 $ */
public class ScoreDoc
{
  private com.zimbra.cs.index.ZimbraScoreDoc mZObject;

  public ScoreDoc(@NotNull Object zObject)
  {
    mZObject = (com.zimbra.cs.index.ZimbraScoreDoc) zObject;
  }

  public DocumentId getDocumentId()
  {
    return new DocumentId(mZObject.getDocumentID());
  }

  public float getScore()
  {
    return mZObject.getScore();
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
public class ScoreDoc
{
  public ScoreDoc(@NotNull Object zObject)
  {
    throw new UnsupportedOperationException();
  }

  public DocumentId getDocumentId()
  {
    throw new UnsupportedOperationException();
  }

  public float getScore()
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