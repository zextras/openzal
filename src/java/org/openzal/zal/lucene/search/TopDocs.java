package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TopDocs
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private com.zimbra.cs.index.ZimbraTopDocs mZObject;
  /* $endif $ */

  public TopDocs(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject = (com.zimbra.cs.index.ZimbraTopDocs) zObject;
    /* $endif $ */
  }

  public int getTotalHits()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.getTotalHits();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public float getMaxScore()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.getMaxScore();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public List<ScoreDoc> getScoreDocs()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    List<ScoreDoc> scoreDocList = new ArrayList<>();

    for( com.zimbra.cs.index.ZimbraScoreDoc doc : mZObject.getScoreDocs() )
    {
      scoreDocList.add(new ScoreDoc(doc));
    }

    return scoreDocList;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public ScoreDoc getScoreDoc(int index)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return new ScoreDoc(mZObject.getScoreDoc(index));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}