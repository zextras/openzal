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
  private final Object       mZObject;
  /* $endif $ */

  @Deprecated
  public IndexStore(@Nonnull Object zObject)
  {
    this(null, zObject);
  }

  public IndexStore(@Nonnull MailboxIndex mailboxIndex, @Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      mMailboxIndex = mailboxIndex;
    }
    /* $endif */

    /* $if ZimbraX == 1 $
    {
      mZObject = (com.zimbra.cs.index.solr.SolrIndex) zObject;
    }
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    {
      mZObject = (com.zimbra.cs.index.LuceneIndex) zObject;
    }
    /* $endif $ */
  }

  public Document createDocument()
  {
    /* $if ZimbraX == 1 $
    {
      return new Document();
    }
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    {
      return new Document();
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public Indexer openIndexer()
    throws IOException, ZimbraException
  {
    try
    {
      /* $if ZimbraX == 1 $
      {
        return new Indexer(this, toZimbra(com.zimbra.cs.index.solr.SolrIndex.class).openIndexer());
      }
      /* $elseif ZimbraVersion >= 8.5.0 $ */
      {
        return new Indexer(this, toZimbra(com.zimbra.cs.index.LuceneIndex.class).openIndexer());
      }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
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
      /* $if ZimbraX == 1 $
      {
        return new IndexSearcher(toZimbra(com.zimbra.cs.index.solr.SolrIndex.class).openSearcher());
      }
      /* $elseif ZimbraVersion >= 8.5.0 $ */
      {
        return new IndexSearcher(toZimbra(com.zimbra.cs.index.LuceneIndex.class).openSearcher());
      }
      /* $else $
      {
        throw new UnsupportedOperationException();
      }
      /* $endif $ */
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
    {
      return mMailboxIndex;
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

  @Deprecated
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

    public Factory(@Nonnull Object zObject)
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
      return new IndexStore(mailbox.getIndex(), mZObject.getIndexStore(mailbox.getMailbox()));
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

    public <T> T toZimbra(@Nonnull Class<T> target)
    {
      /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
      return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
    }
  }
}

