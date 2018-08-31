package org.openzal.zal.lucene.index;

import com.zimbra.common.service.ServiceException;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.Mailbox;
import org.openzal.zal.lib.ZalWrapper;
import org.openzal.zal.lucene.search.LuceneSearcher;

import java.io.IOException;

public class LuceneIndexStore
  implements ZalWrapper<com.zimbra.cs.index.LuceneIndex>
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

  @Override
  public com.zimbra.cs.index.LuceneIndex toZimbra()
  {
    return mZObject;
  }

  @Override
  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }


  public static class Factory
    implements ZalWrapper<com.zimbra.cs.index.LuceneIndex.Factory>
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

    @Override
    public com.zimbra.cs.index.LuceneIndex.Factory toZimbra()
    {
      return mZObject;
    }

    @Override
    public <T> T toZimbra(@NotNull Class<T> target)
    {
      return target.cast(mZObject);
    }
  }

}
