package org.openzal.zal.lucene.index;

import com.zimbra.common.service.ServiceException;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.Mailbox;
import org.openzal.zal.lib.ZalWrapper;
import org.openzal.zal.lucene.search.LuceneSearcher;

import java.io.IOException;

/* $if ZimbraVersion >= 8.5.0 $ */
public class LuceneIndexStore
{
  private final com.zimbra.cs.index.LuceneIndex mZObject;

  public LuceneIndexStore(@NotNull Object zObject)
  {
    mZObject = (com.zimbra.cs.index.LuceneIndex) zObject;
  }

  public LuceneIndexer openIndexer()
    throws IOException
  {
    return new LuceneIndexer(mZObject.openIndexer());
  }

  public LuceneSearcher openSearcher()
    throws IOException
  {
    return new LuceneSearcher(mZObject.openSearcher());
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

    public LuceneIndexStore getIndexStore(Mailbox mailbox)
      throws ServiceException
    {
      return new LuceneIndexStore(mZObject.getIndexStore(mailbox.getMailbox()));
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
public class LuceneIndexStore
{

  public LuceneIndexStore(@NotNull Object zObject)
  {
    throw new UnsupportedOperationException();
  }

  public LuceneIndexer openIndexer()
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public LuceneSearcher openSearcher()
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

    public LuceneIndexStore getIndexStore(Mailbox mailbox)
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
