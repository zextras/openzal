package org.openzal.zal.lucene.index;

import com.zimbra.common.service.ServiceException;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.Mailbox;
import org.openzal.zal.lucene.search.IndexSearcher;

import java.io.IOException;

public class IndexStore
{
  /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
  private final com.zimbra.cs.index.LuceneIndex mZObject;
  /* $endif $ */

  public IndexStore(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mZObject = (com.zimbra.cs.index.LuceneIndex) zObject;
    /* $endif $ */
  }

  public Indexer openIndexer()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return new Indexer(mZObject.openIndexer());
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public IndexSearcher openSearcher()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return new IndexSearcher(mZObject.openSearcher());
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return mZObject.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }


  public static class Factory
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    private final com.zimbra.cs.index.LuceneIndex.Factory mZObject;
    /* $endif $ */

    public Factory()
    {
      /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
      this(new com.zimbra.cs.index.LuceneIndex.Factory());
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
    }

    public Factory(@NotNull Object zObject)
    {
      /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
      mZObject = (com.zimbra.cs.index.LuceneIndex.Factory) zObject;
      /* $else $
      throw new UnsupportedOperationException();
      /* $endif $ */
    }

    public IndexStore getIndexStore(Mailbox mailbox)
      throws ServiceException
    {
      /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
      return new IndexStore(mZObject.getIndexStore(mailbox.getMailbox()));
      /* $else $
      throw new UnsupportedOperationException();
      /* $endif $ */
    }

    @Override
    public String toString()
    {
      /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
      return mZObject.toString();
      /* $else $
      throw new UnsupportedOperationException();
      /* $endif $ */
    }

    public <T> T toZimbra(@NotNull Class<T> target)
    {
      /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
      return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
    }
  }
}

