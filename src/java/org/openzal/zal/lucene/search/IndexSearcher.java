package org.openzal.zal.lucene.search;

import com.zimbra.common.service.ServiceException;
import javax.annotation.Nonnull;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.lucene.document.Document;
import org.openzal.zal.lucene.document.DocumentId;

import java.io.Closeable;
import java.io.IOException;

public class IndexSearcher
  implements Closeable
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final com.zimbra.cs.index.ZimbraIndexSearcher mZObject;
  /* $endif $ */
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final IndexReader                             mReader;
  /* $endif $ */

  public IndexSearcher(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject = (com.zimbra.cs.index.ZimbraIndexSearcher) zObject;
    mReader = new IndexReader(mZObject.getIndexReader());
    /* $endif $ */
  }

  private IndexReader getIndexReader()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mReader;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public TopDocs search(Query query, int limit)
    throws IOException
  {
    /* $if ZimbraX == 1 $
    try
    {
      /* $endif $ */
      /* $if ZimbraVersion >= 8.5.0 $ */
      return new TopDocs(mZObject.search(query.toZimbra(org.apache.lucene.search.Query.class), limit));
      /* $else $
      throw new UnsupportedOperationException();
      /* $endif $ */
      /* $if ZimbraX == 1 $
    }
    catch( ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $endif $ */
  }

  public int countDocuments()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return getIndexReader().countDocuments();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public int countDeletedDocuments()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return getIndexReader().countDeletedDocument();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public Document getDocument(DocumentId id)
    throws IOException
  {
    /* $if ZimbraX == 1 $
    try
    {
    /* $endif $ */
    /* $if ZimbraVersion >= 8.5.0 $ */
    return new Document(mZObject.doc(id.toZimbra(com.zimbra.cs.index.ZimbraIndexDocumentID.class)));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
    /* $if ZimbraX == 1 $
    }
    catch( ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $endif $ */
  }

  @Override
  public void close()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject.close();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
