package org.openzal.zal.index.x;

import org.openzal.zal.lucene.search.ScoreDoc;

import javax.annotation.Nonnull;

public abstract class IndexSearchResult
  extends ScoreDoc
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final IndexDocument mIndexDocument;
  private final com.zimbra.cs.index.ZimbraScoreDoc mZObject;
  /* $endif $ */

  public IndexSearchResult(IndexDocument indexDocument, @Nonnull Object zObject)
  {
    super(null);

    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      mIndexDocument = indexDocument;
      mZObject = (com.zimbra.cs.index.ZimbraScoreDoc) zObject;
    }
    /* $endif $ */
  }

  public IndexDocumentId getDocumentId()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      return new IndexDocumentId(mZObject.getDocumentID());
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public float getScore()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      return mZObject.getScore();
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public IndexDocument getDocument()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      return mIndexDocument;
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
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