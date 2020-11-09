package org.openzal.zal.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.openzal.zal.lucene.document.DocumentId;
import org.openzal.zal.lucene.document.Document;

public class TopDocs
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private IndexSearcher                     mIndexSearcher;
  private com.zimbra.cs.index.ZimbraTopDocs mTopDocs;
  /* $endif $ */

  @Deprecated
  public TopDocs(@Nonnull Object zObject)
  {
    this(null, zObject);
  }

  public TopDocs(IndexSearcher indexSearcher, @Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mIndexSearcher = indexSearcher;
    mTopDocs = (com.zimbra.cs.index.ZimbraTopDocs) zObject;
    /* $endif $ */
  }

  public int getTotalHits()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mTopDocs.getTotalHits();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public float getMaxScore()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mTopDocs.getMaxScore();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public List<ScoreDoc> getScoreDocs()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    List<ScoreDoc> scoreDocList = new ArrayList<>();

    for(int i = 0; i < mTopDocs.getScoreDocs().size(); i++ )
    {
      scoreDocList.add(getScoreDoc(i));
    }

    return scoreDocList;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public ScoreDoc getScoreDoc(int index)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    com.zimbra.cs.index.ZimbraScoreDoc scoreDoc = mTopDocs.getScoreDoc(index);
    /* $endif */

    /* $if ZimbraX == 1 $
    return new ScoreDoc(new Document(mTopDocs.getDoc(index)), scoreDoc);
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    return new ScoreDoc(mIndexSearcher.getDocument(new DocumentId(scoreDoc.getDocumentID())), scoreDoc);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mTopDocs.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mTopDocs);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
