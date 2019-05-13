package org.openzal.zal.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openzal.zal.lucene.document.Document;
import org.openzal.zal.lucene.document.DocumentId;

import javax.annotation.Nonnull;

public class TopDocs
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private IndexSearcher                     mIndexSearcher;
  private com.zimbra.cs.index.ZimbraTopDocs mZObject;
  /* $endif $ */

  @Deprecated
  public TopDocs(@Nonnull Object zObject)
  {
    this(null, zObject);
  }

  public TopDocs(IndexSearcher indexSearcher, @Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      mIndexSearcher = indexSearcher;
      mZObject = (com.zimbra.cs.index.ZimbraTopDocs) zObject;
    }
    /* $endif $ */
  }

  public int getTotalHits()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      return mZObject.getTotalHits();
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public float getMaxScore()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      return mZObject.getMaxScore();
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public List<ScoreDoc> getScoreDocs()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      List<ScoreDoc> scoreDocList = new ArrayList<>();

      for(int i = 0; i < getTotalHits(); i++ )
      {
        scoreDocList.add(getScoreDoc(i));
      }

      return scoreDocList;
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public ScoreDoc getScoreDoc(int index)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    com.zimbra.cs.index.ZimbraScoreDoc scoreDoc = mZObject.getScoreDoc(index);
    /* $endif */

    /* $if ZimbraX == 1 $
    {
      return new ScoreDoc(new Document(mZObject.getDoc(index)), scoreDoc);
    }
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    {
      return new ScoreDoc(mIndexSearcher.getDocument(new DocumentId(scoreDoc.getDocumentID())), scoreDoc);
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