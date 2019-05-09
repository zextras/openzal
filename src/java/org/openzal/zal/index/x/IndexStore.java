package org.openzal.zal.index.x;

import java.io.IOException;
import org.openzal.zal.MailboxIndex;
import org.openzal.zal.exceptions.ZimbraException;

import javax.annotation.Nonnull;

public abstract class IndexStore
  extends org.openzal.zal.lucene.index.IndexStore
{
  private MailboxIndex mMailboxIndex;

  /* $if ZimbraVersion >= 8.5.0 $ */
  private final com.zimbra.cs.index.IndexStore mZObject;
  /* $endif $ */

  public IndexStore(@Nonnull MailboxIndex mailboxIndex, @Nonnull Object zObject)
  {
    super(null);

    mMailboxIndex = mailboxIndex;

    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      mZObject = (com.zimbra.cs.index.IndexStore) zObject;
    }
    /* $endif $ */
  }

  public abstract IndexDocument createDocument();

  public abstract Indexer openIndexer()
    throws IOException, ZimbraException;

  public abstract IndexSearcher openSearcher()
    throws IOException, ZimbraException;

  public MailboxIndex getMailboxIndex()
  {
    return mMailboxIndex;
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

  @Override
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

