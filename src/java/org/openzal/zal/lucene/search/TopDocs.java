package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/* $if ZimbraVersion >= 8.5.0 $ */
public class TopDocs
{
  private com.zimbra.cs.index.ZimbraTopDocs mZObject;

  public TopDocs(@NotNull Object zObject)
  {
    mZObject = (com.zimbra.cs.index.ZimbraTopDocs) zObject;
  }

  public int getTotalHits()
  {
    return mZObject.getTotalHits();
  }

  public float getMaxScore()
  {
    return mZObject.getMaxScore();
  }

  public List<ScoreDoc> getScoreDocs()
  {
    List<ScoreDoc> scoreDocList = new ArrayList<>();

    for( com.zimbra.cs.index.ZimbraScoreDoc doc : mZObject.getScoreDocs())
    {
      scoreDocList.add(new ScoreDoc(doc));
    }

    return scoreDocList;
  }

  public ScoreDoc getScoreDoc(int index)
  {
    return new ScoreDoc(mZObject.getScoreDoc(index));
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
public class TopDocs
{
  public TopDocs(@NotNull Object zObject)
  {
    throw new UnsupportedOperationException();
  }

  public int getTotalHits()
  {
    throw new UnsupportedOperationException();
  }

  public float getMaxScore()
  {
    throw new UnsupportedOperationException();
  }

  public List<ScoreDoc> getScoreDocs()
  {
    throw new UnsupportedOperationException();
  }

  public ScoreDoc getScoreDoc(int index)
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