package org.openzal.zal.lucene.index;

import com.zimbra.common.service.ServiceException;
import java.io.IOException;
import org.openzal.zal.Mailbox;
import org.openzal.zal.MailboxIndex;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lucene.document.Document;
import org.openzal.zal.lucene.search.IndexSearcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IndexStore
{
  private final MailboxIndex mMailboxIndex;
  private final com.zimbra.cs.index.LuceneIndex          mIndex;

  public IndexStore(@Nonnull Object zObject)
  {
    this(null, zObject);
  }

  public IndexStore(MailboxIndex mailboxIndex, @Nonnull Object zObject)
  {
    mMailboxIndex = mailboxIndex;

    mIndex = (com.zimbra.cs.index.LuceneIndex) zObject;
  }

  public Document createDocument()
  {
    return new Document();
  }

  public Indexer openIndexer()
    throws IOException, ZimbraException
  {
    try
    {
      return new Indexer(this, mIndex.openIndexer());
    }
    catch( Exception e )
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public IndexSearcher openSearcher()
    throws IOException
  {
    try
    {
      return new IndexSearcher(mIndex.openSearcher());
    }
    catch( Exception e )
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public MailboxIndex getMailboxIndex()
  {
    return mMailboxIndex;
  }

  @Override
  public String toString()
  {
      return mIndex.toString();
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    return target.cast(mIndex);
  }

  public static class Factory
  {
    private final com.zimbra.cs.index.LuceneIndex.Factory mFactory;

    public Factory()
    {
      this(new com.zimbra.cs.index.LuceneIndex.Factory());
    }

    public Factory(@Nonnull Object zObject)
    {
      mFactory = (com.zimbra.cs.index.LuceneIndex.Factory) zObject;
    }

    public IndexStore getIndexStore(Mailbox mailbox)
      throws ServiceException
    {
      return new IndexStore(mailbox.getIndex(), mFactory.getIndexStore(mailbox.getMailbox()));
    }

    @Override
    public String toString()
    {
      return mFactory.toString();
    }

    public <T> T toZimbra(@Nonnull Class<T> target)
    {
      return target.cast(mFactory);
    }
  }
}

