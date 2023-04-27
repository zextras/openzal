package org.openzal.zal.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.openzal.zal.lucene.document.DocumentId;
import org.openzal.zal.lucene.document.Document;

public class TopDocs
{
  private IndexSearcher                     mIndexSearcher;
  private com.zimbra.cs.index.ZimbraTopDocs mTopDocs;

  @Deprecated
  public TopDocs(@Nonnull Object zObject)
  {
    this(null, zObject);
  }

  public TopDocs(IndexSearcher indexSearcher, @Nonnull Object zObject)
  {
    mIndexSearcher = indexSearcher;
    mTopDocs = (com.zimbra.cs.index.ZimbraTopDocs) zObject;
  }

  public int getTotalHits()
  {
    return mTopDocs.getTotalHits();
  }

  public float getMaxScore()
  {
    return mTopDocs.getMaxScore();
  }

  public List<ScoreDoc> getScoreDocs()
    throws IOException
  {
    List<ScoreDoc> scoreDocList = new ArrayList<>();

    for(int i = 0; i < mTopDocs.getScoreDocs().size(); i++ )
    {
      scoreDocList.add(getScoreDoc(i));
    }

    return scoreDocList;
  }

  public ScoreDoc getScoreDoc(int index)
    throws IOException
  {
    com.zimbra.cs.index.ZimbraScoreDoc scoreDoc = mTopDocs.getScoreDoc(index);

    return new ScoreDoc(mIndexSearcher.getDocument(new DocumentId(scoreDoc.getDocumentID())), scoreDoc);
  }

  @Override
  public String toString()
  {
    return mTopDocs.toString();
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    return target.cast(mTopDocs);
  }
}
