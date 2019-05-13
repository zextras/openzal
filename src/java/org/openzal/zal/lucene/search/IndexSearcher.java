package org.openzal.zal.lucene.search;

import com.zimbra.common.service.ServiceException;
import java.io.Closeable;
import java.io.IOException;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.lucene.document.Document;
import org.openzal.zal.lucene.document.DocumentId;

import javax.annotation.Nonnull;

public class IndexSearcher
  implements Closeable
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final com.zimbra.cs.index.ZimbraIndexSearcher mZObject;

  private final IndexReader                             mReader;
  /* $endif $ */

  public IndexSearcher(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      mZObject = (com.zimbra.cs.index.ZimbraIndexSearcher) zObject;
      mReader = new IndexReader(mZObject.getIndexReader());
    }
    /* $endif $ */
  }

  private IndexReader getIndexReader()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    {
      return mReader;
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  @Deprecated
  public TopDocs search(Query query, int limit)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    {
      return new TopDocs(this, mZObject.search(query.toZimbra(org.apache.lucene.search.Query.class), limit));
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public TopDocs search(Query query, int limit, String fieldId, String... fetchFields)
    throws IOException
  {
    /* $if ZimbraX == 1 $
    {
      try
      {
        return new TopDocs(this, mZObject.search(query.toZimbra(org.apache.lucene.search.Query.class), null, limit, null, fieldId, fetchFields));
      }
      catch( ServiceException e )
      {
        throw ExceptionWrapper.wrap(e);
      }
    }
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    {
      return search(query, limit);
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public int countDocuments()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    {
      return getIndexReader().countDocuments();
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public int countDeletedDocuments()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    {
      return getIndexReader().countDeletedDocument();
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

  public Document getDocument(DocumentId id)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    {
      return new Document(mZObject.doc(id.toZimbra(com.zimbra.cs.index.ZimbraIndexDocumentID.class)));
    }
    /* $else $
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }

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
