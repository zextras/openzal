package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lib.ZalWrapper;

import java.io.IOException;

public class LuceneSearcher
  implements ZalWrapper<com.zimbra.cs.index.ZimbraIndexSearcher>
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
    return new LuceneTopDocs(mZObject.search(query.toZimbra(), limit));
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

  @Override
  public com.zimbra.cs.index.ZimbraIndexSearcher toZimbra()
  {
    return mZObject;
  }

  @Override
  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }
}
