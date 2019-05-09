package org.openzal.zal.index.x;

import java.util.Iterator;

import javax.annotation.Nonnull;

public abstract class IndexSearchResults
  implements Iterable<IndexSearchResult>
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private com.zimbra.cs.index.ZimbraTopDocs mZObject;
  /* $endif $ */

  public IndexSearchResults(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
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

  public abstract IndexSearchResult getResult(int index);

  @Override
  public Iterator<IndexSearchResult> iterator()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      final int maxHits = getTotalHits();
      return new Iterator<IndexSearchResult>()
      {
        private int mIndex = 0;

        @Override
        public boolean hasNext()
        {
          return mIndex < maxHits;
        }

        @Override
        public IndexSearchResult next()
        {
          return getResult(mIndex++);
        }
      };
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