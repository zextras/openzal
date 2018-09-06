package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lucene.document.Document;
import org.openzal.zal.lucene.document.DocumentId;

import java.io.Closeable;
import java.io.IOException;

/* $if ZimbraVersion >= 8.5.0 $ */
public class IndexSearcher
  implements Closeable
{
  private final com.zimbra.cs.index.ZimbraIndexSearcher mZObject;
  private final IndexReader mReader;

  public IndexSearcher(@NotNull Object zObject) {
    mZObject = (com.zimbra.cs.index.ZimbraIndexSearcher) zObject;
    mReader = new IndexReader(mZObject.getIndexReader());
  }

  public IndexReader getIndexReader()
  {
    return mReader;
  }

  public TopDocs search(Query query, int limit)
    throws IOException
  {
    return new TopDocs(mZObject.search(query.toZimbra(org.apache.lucene.search.Query.class), limit));
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
    return new Document(mZObject.doc(id.toZimbra(com.zimbra.cs.index.ZimbraIndexDocumentID.class)));
  }

  @Override
  public void close()
    throws IOException
  {
    mZObject.close();
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
/* $else $
public class IndexSearcher
  implements Closeable
{
  public IndexSearcher(@NotNull Object zObject) {
    throw new UnsupportedOperationException();
  }

  public IndexReader getIndexReader()
  {
    throw new UnsupportedOperationException();
  }

  public TopDocs search(Query query, int limit)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public int countDocuments()
  {
    throw new UnsupportedOperationException();
  }

  public int countDeletedDocuments()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close()
  {
    throw new UnsupportedOperationException();
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    throw new UnsupportedOperationException();
  }
}
/* $endif $ */
