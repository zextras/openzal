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
  private final com.zimbra.cs.index.ZimbraIndexSearcher mIndexSearcher;
  private final IndexReader                             mReader;

  public IndexSearcher(@Nonnull Object zObject)
  {
    mIndexSearcher = (com.zimbra.cs.index.ZimbraIndexSearcher) zObject;
    mReader = new IndexReader(mIndexSearcher.getIndexReader());
  }

  private IndexReader getIndexReader()
  {
    return mReader;
  }

  public TopDocs search(Query query, int limit)
    throws IOException
  {
    return new TopDocs(this, mIndexSearcher.search(query.toZimbra(org.apache.lucene.search.Query.class), limit));
  }

  public TopDocs search(Query query, int limit, String fieldId, String... fetchFields)
    throws IOException
  {
    return search(query, limit);
  }

  public int countDocuments()
  {
    return getIndexReader().countDocuments();
  }

  public int countDeletedDocuments()
  {
    return getIndexReader().countDeletedDocument();
  }

  public Document getDocument(DocumentId id)
    throws IOException
  {
    return new Document(mIndexSearcher.doc(id.toZimbra(com.zimbra.cs.index.ZimbraIndexDocumentID.class)));
  }

  @Override
  public void close()
    throws IOException
  {
    mIndexSearcher.close();
  }

  @Override
  public String toString()
  {
    return mIndexSearcher.toString();
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    return target.cast(mIndexSearcher);
  }
}
