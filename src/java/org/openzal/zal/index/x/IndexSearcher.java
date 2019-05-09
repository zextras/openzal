package org.openzal.zal.index.x;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.openzal.zal.lucene.search.Query;
import org.openzal.zal.lucene.search.TopDocs;

import javax.annotation.Nonnull;

public abstract class IndexSearcher
  extends org.openzal.zal.lucene.search.IndexSearcher
  implements Closeable
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private       String                                  mAccountId;
  private final com.zimbra.cs.index.ZimbraIndexSearcher mZObject;
  /* $endif $ */

  public IndexSearcher(@Nonnull String accountId, @Nonnull Object zObject)
  {
    super(null);

    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      mAccountId = accountId;
      mZObject = (com.zimbra.cs.index.ZimbraIndexSearcher) zObject;
    }
    /* $endif $ */
  }

  @Override
  @Deprecated
  public TopDocs search(Query query, int limit)
    throws IOException
  {
    throw new UnsupportedEncodingException();
  }

  public abstract IndexSearchResults search(Query query, int maxResults, String fieldId, String... fetchFields)
    throws IOException;

  @Override
  public void close()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      mZObject.close();
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public String getAccountId()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      return mAccountId;
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