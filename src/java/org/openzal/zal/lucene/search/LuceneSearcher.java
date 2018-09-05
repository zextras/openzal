package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/* $if ZimbraVersion >= 8.5.0 $ */
public class LuceneSearcher
{
  private final com.zimbra.cs.index.ZimbraIndexSearcher mZObject;

  public LuceneSearcher(@NotNull Object zObject) {
    mZObject = (com.zimbra.cs.index.ZimbraIndexSearcher) zObject;
  }

  public LuceneIndexReader getIndexReader()
  {
    return new LuceneIndexReader(mZObject.getIndexReader());
  }

  public LuceneTopDocs search(LuceneQuery query, int limit)
    throws IOException
  {
    return new LuceneTopDocs(mZObject.search(query.toZimbra(org.apache.lucene.search.Query.class), limit));
  }

  public int countDocuments()
  {
    return getIndexReader().countDocuments();
  }

  public int countDeletedDocuments()
  {
    return getIndexReader().countDeletedDocument();
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
public class LuceneSearcher
{
  public LuceneSearcher(@NotNull Object zObject) {
    throw new UnsupportedOperationException();
  }

  public LuceneIndexReader getIndexReader()
  {
    throw new UnsupportedOperationException();
  }

  public LuceneTopDocs search(LuceneQuery query, int limit)
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

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    throw new UnsupportedOperationException();
  }
}
/* $endif $ */
