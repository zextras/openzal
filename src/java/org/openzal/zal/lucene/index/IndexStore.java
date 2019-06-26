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
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final MailboxIndex mMailboxIndex;
  /* $if ZimbraX == 0 $ */
  private final com.zimbra.cs.index.LuceneIndex          mIndex;
  /* $else $
  private final com.zimbra.cs.index.solr.SolrIndex       mIndex;
  /* $endif $ */

  public IndexStore(@Nonnull Object zObject)
  {
    this(null, zObject);
  }

  public IndexStore(MailboxIndex mailboxIndex, @Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mMailboxIndex = mailboxIndex;
    /* $endif */

    /* $if ZimbraX == 1 $
    mIndex = (com.zimbra.cs.index.solr.SolrIndex) zObject;
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    mIndex = (com.zimbra.cs.index.LuceneIndex) zObject;
    /* $else$
    mIndex = null;
    /* $endif $ */
  }

  public Document createDocument()
  {
    /* $if ZimbraX == 1 || ZimbraVersion >= 8.5.0 $ */
    return new Document();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
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
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mMailboxIndex;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
      return mIndex.toString();
    /* $else $
      throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mIndex);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public static class Factory
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    private final com.zimbra.cs.index.LuceneIndex.Factory mFactory;
    /* $endif $ */

    public Factory()
    {
      /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
      this(new com.zimbra.cs.index.LuceneIndex.Factory());
      /* $else $
      throw new UnsupportedOperationException();
      /* $endif $ */
    }

    public Factory(@Nonnull Object zObject)
    {
      /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
      mFactory = (com.zimbra.cs.index.LuceneIndex.Factory) zObject;
      /* $else $
      throw new UnsupportedOperationException();
      /* $endif $ */
    }

    public IndexStore getIndexStore(Mailbox mailbox)
      throws ServiceException
    {
      /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
      return new IndexStore(mailbox.getIndex(), mFactory.getIndexStore(mailbox.getMailbox()));
      /* $else $
      throw new UnsupportedOperationException();
      /* $endif $ */
    }

    @Override
    public String toString()
    {
      /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
      return mFactory.toString();
      /* $else $
      throw new UnsupportedOperationException();
      /* $endif $ */
    }

    public <T> T toZimbra(@Nonnull Class<T> target)
    {
      /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
      return target.cast(mFactory);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
    }
  }
}

