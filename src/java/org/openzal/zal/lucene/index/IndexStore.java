package org.openzal.zal.lucene.index;

import com.zimbra.common.service.ServiceException;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.Mailbox;
import org.openzal.zal.lucene.search.IndexSearcher;

import java.io.IOException;

/* $if ZimbraVersion >= 8.5.0 $ */
public class IndexStore
{
  private final com.zimbra.cs.index.LuceneIndex mZObject;

  public IndexStore(@NotNull Object zObject)
  {
    mZObject = (com.zimbra.cs.index.LuceneIndex) zObject;
  }

  public Indexer openIndexer()
    throws IOException
  {
    return new Indexer(mZObject.openIndexer());
  }

  public IndexSearcher openSearcher()
    throws IOException
  {
    return new IndexSearcher(mZObject.openSearcher());
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


  public static class Factory
  {
    private final com.zimbra.cs.index.LuceneIndex.Factory mZObject;

    public Factory()
    {
      this(new com.zimbra.cs.index.LuceneIndex.Factory());
    }

    public Factory(@NotNull Object zObject)
    {
      mZObject = (com.zimbra.cs.index.LuceneIndex.Factory) zObject;
    }

    public IndexStore getIndexStore(Mailbox mailbox)
      throws ServiceException
    {
      return new IndexStore(mZObject.getIndexStore(mailbox.getMailbox()));
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
}

/* $else $
public class IndexStore
{

  public IndexStore(@NotNull Object zObject)
  {
    throw new UnsupportedOperationException();
  }

  public Indexer openIndexer()
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public IndexSearcher openSearcher()
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    throw new UnsupportedOperationException();
  }


  public static class Factory
  {
    public Factory()
    {
      throw new UnsupportedOperationException();
    }

    public Factory(@NotNull Object zObject)
    {
      throw new UnsupportedOperationException();
    }

    public IndexStore getIndexStore(Mailbox mailbox)
      throws ServiceException
    {
      throw new UnsupportedOperationException();
    }

    public <T> T toZimbra(@NotNull Class<T> target)
    {
      throw new UnsupportedOperationException();
    }
  }
}
/* $endif $ */
